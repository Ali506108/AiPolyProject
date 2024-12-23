package com.ai.AVAI.Service;

import com.ai.AVAI.module.Avatar;
import com.ai.AVAI.repository.AvatarRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.xml.bind.DatatypeConverter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class TogetherAiService {
    private final WebClient webClient;
    private final AvatarRepository avatarRepository;

    @Value("${together.ai.apiKey}")
    private String apiKey;

    @Autowired
    public TogetherAiService(WebClient webClient, AvatarRepository avatarRepository) {
        this.webClient = webClient;
        this.avatarRepository = avatarRepository;
    }


    // Method to generate an image
    public String generateImage(String model, String prompt, int width, int height, int steps, int n, String outputFileName) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "prompt", prompt,
                "width", width,
                "height", height,
                "steps", steps,
                "n", n,
                "response_format", "b64_json"
        );

        String response = webClient.post()
                .uri("/images/generations")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            saveBase64ImageToFile(response, outputFileName);
        } catch (IOException e) {
            throw new RuntimeException("Error saving the image: " + e.getMessage(), e);
        }

        return "Image saved as: " + outputFileName;
    }

    public void saveAvatar(Avatar avatar) {
        avatarRepository.save(avatar);
    }

    private void saveBase64ImageToFile(String response, String outputFileName) throws IOException {
        String base64Image = parseBase64FromResponse(response);
        byte[] imageBytes = DatatypeConverter.parseBase64Binary(base64Image);
        try (FileOutputStream fos = new FileOutputStream(outputFileName)) {
            fos.write(imageBytes);
        }
    }

    private String parseBase64FromResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataNode = rootNode.path("data").get(0);

            if (dataNode == null || dataNode.isMissingNode()) {
                throw new IllegalArgumentException("The 'data' field is missing or empty in the response.");
            }

            JsonNode b64Node = dataNode.path("b64_json");
            if (b64Node == null || b64Node.isMissingNode()) {
                throw new IllegalArgumentException("The 'b64_json' field is missing in 'data'.");
            }

            return b64Node.asText();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON response: " + e.getMessage(), e);
        }
    }

    // Method to generate a text response (chat)
    public String generateResponse(String prompt) {
        try {
            // Создание тела запроса
            Map<String, Object> requestBody = Map.of(
                    "model", "meta-llama/Llama-3.3-70B-Instruct-Turbo",
                    "messages", List.of(Map.of("role", "user", "content", prompt)),
                    "max_tokens", 150,
                    "temperature", 0.7
            );

            // Выполнение запроса
            String response = webClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Парсинг ответа и извлечение content
            return extractContentFromResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при запросе к AI: " + e.getMessage();
        }
    }


    private String extractContentFromResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode choicesNode = rootNode.path("choices");

            if (!choicesNode.isArray() || choicesNode.isEmpty()) {
                return "AI не предоставил ответ.";
            }

            // Извлечение content из первого элемента массива choices
            JsonNode contentNode = choicesNode.get(0).path("message").path("content");
            if (contentNode.isMissingNode()) {
                return "Ответ AI пуст.";
            }

            return contentNode.asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при обработке ответа AI: " + e.getMessage();
        }
    }
}
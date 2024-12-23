package com.ai.AVAI.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Service
public class ImageGenerationService {

    private final WebClient webClient;
    private final String apiKey = "a906cae7180dfb5eb10f390c17bf1a3ec8584bae5084b8172bfb2323c0be4ea0";

    public ImageGenerationService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.together.xyz/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    public String generateImageAsBase64(String model, String prompt, int width, int height, int steps, int n) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "prompt", prompt,
                "width", width,
                "height", height,
                "steps", steps,
                "n", n,
                "response_format", "b64_json"
        );

        try {
            String response = webClient.post()
                    .uri("/images/generations")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Извлекаем Base64 строку из ответа
            return extractBase64FromResponse(response);
        } catch (WebClientResponseException e) {
            throw new RuntimeException("API Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected Error: " + e.getMessage(), e);
        }
    }

    private String extractBase64FromResponse(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.path("data").path(0).path("b64_json").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse API response: " + e.getMessage(), e);
        }
    }
}

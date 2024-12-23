package com.ai.AVAI.controler;

import com.ai.AVAI.JwtUtil.JwtUtil;
import com.ai.AVAI.Service.ChatService;
import com.ai.AVAI.Service.ImageGenerationService;
import com.ai.AVAI.Service.TogetherAiService;
import com.ai.AVAI.module.Avatar;
import com.ai.AVAI.module.Chat;
import com.ai.AVAI.module.Message;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@CrossOrigin("http://localhost:3000")
public class ChatController {

    private final ChatService chatService;
    private final TogetherAiService aiService;

    @Autowired
    public ChatController(ChatService chatService, TogetherAiService aiService) {
        this.chatService = chatService;
        this.aiService = aiService;
    }

    @PostMapping("/create")
    public Chat createChat(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Claims claims = JwtUtil.decodeJWT(token);
        String userId = claims.get("Id").toString();

        return chatService.createChat(userId);
    }

    @GetMapping("/all")
    public List<Chat> allChat() {
        return chatService.getAllChats();
    }

    @Autowired
    private ImageGenerationService imageGenerationService;

    @PostMapping("/{chatId}/send")
    public ResponseEntity<?> sendMessage(@PathVariable long chatId, @RequestBody String userMessage) {
        try {
            // Сохраняем сообщение пользователя
            Message userMsg = chatService.addMessage(chatId, userMessage, true);

            // Генерируем ответ от ИИ
            String aiResponse = aiService.generateResponse(userMessage);

            // Сохраняем ответ ИИ
            Message aiMessage = chatService.addMessage(chatId, aiResponse, false);

            String base64Image = null;

            // Проверяем, нужно ли сгенерировать изображение
            if (userMessage.toLowerCase().contains("image")) {
                String model = "black-forest-labs/FLUX.1-dev";

                // Генерация изображения через ImageGenerationService
                base64Image = imageGenerationService.generateImageAsBase64(
                        model, userMessage, 1024, 768, 28, 1
                );
            }

            // Формируем ответ
            return ResponseEntity.ok(Map.of(
                    "userMessage", userMsg,
                    "aiResponse", aiMessage,
                    "image", base64Image != null ? "data:image/png;base64," + base64Image : null
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }





    @GetMapping("/{chatId}/history")
    public List<Message> getChatHistory(@PathVariable long chatId) {
        return chatService.getMessages(chatId);
    }

    @PostMapping("/ask")
    public String askAI(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        if (prompt == null || prompt.trim().isEmpty()) {
            return "Prompt is missing or empty.";
        }
        String response = aiService.generateResponse(prompt);
        if (response == null || response.isEmpty()) {
            return "No response from AI. Please try again later.";
        }
        return response;
    }


}

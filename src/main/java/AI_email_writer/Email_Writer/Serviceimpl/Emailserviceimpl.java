package AI_email_writer.Email_Writer.Serviceimpl;

import AI_email_writer.Email_Writer.Entity.EmailRequest;
import AI_email_writer.Email_Writer.Service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class Emailserviceimpl implements EmailService {

    private final WebClient wc;
    private final String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Emailserviceimpl(WebClient.Builder webClientbuilder, @Value("${open-router.api.url}") String baseURL, @Value("${open-router.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.wc = webClientbuilder.baseUrl(baseURL).build();
    }

    @Override
    public String generateEmailReply(EmailRequest er) {
        String prompt = createPrompt(er);
        String requestbody;
        try {
            Map<String, Object> userMessage = new LinkedHashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", "google/gemini-2.0-flash-001");
            body.put("messages", List.of(userMessage));

            requestbody = objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize OpenRouter request", e);
        }

        String response = wc.post()
                .uri(uriBuilder -> uriBuilder.path("/v1/chat/completions").build())
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestbody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return extractEmailFromResponse(response);
    }

    private String extractEmailFromResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing response";
        }
    }

    private String createPrompt(EmailRequest er) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Write a email reply to the following email");
        if (er.getSenderName() != null && !er.getSenderName().isBlank()) {
            prompt.append(" Writer/signature name: ").append(er.getSenderName().trim()).append(".");
        }
        if (er.getTone() != null && !er.getTone().isEmpty()) {
            prompt.append("Use a").append(er.getTone()).append("tone.");
        }
        prompt.append("original email:\n").append(er.getEmailContent());
        return prompt.toString();
    }
}
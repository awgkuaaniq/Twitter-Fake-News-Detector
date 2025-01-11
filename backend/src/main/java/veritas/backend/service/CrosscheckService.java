package veritas.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import veritas.common.model.CrosscheckResult;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;

@Service
public class CrosscheckService {
    @Value("${api.key.groq}")
    private String groqKey;
    
    @Value("${api.key.tavily}")
    private String tavilyKey;

    @Autowired
    private RestTemplate restTemplate;

    public CrosscheckResult crosscheckContent(String body) {
        String summary = summariseBody(body);
        JsonNode searchResult = searchArticles(summary);
        
        if (searchResult != null && searchResult.has("results") && searchResult.get("results").size() > 0) {
            // Find article with highest score
            JsonNode bestMatch = null;
            double highestScore = 0;
            
            for (JsonNode article : searchResult.get("results")) {
                double score = article.get("score").asDouble();
                if (score > highestScore) {
                    highestScore = score;
                    bestMatch = article;
                }
            }
            
            if (bestMatch != null) {
                return new CrosscheckResult(
                    bestMatch.get("title").asText(),
                    bestMatch.get("content").asText(),
                    bestMatch.get("url").asText(),
                    highestScore  // Use the score directly as probability
                );
            }
        }
        
        // Return default result if no matches found
        return new CrosscheckResult(
            "No relevant articles found",
            "Unable to verify this tweet's content",
            "",
            0.0
        );
    }

    private String summariseBody(String input) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(groqKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama3-8b-8192");
        requestBody.put("messages", Arrays.asList(
            Map.of("role", "system", "content", SYSTEM_PROMPT),
            Map.of("role", "user", "content", input)
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            "https://api.groq.com/openai/v1/chat/completions",
            HttpMethod.POST,
            request,
            JsonNode.class
        );

        return response.getBody()
            .get("choices").get(0)
            .get("message").get("content").asText();
    }

    private JsonNode searchArticles(String summary) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tavilyKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", summary);
        requestBody.put("topic", "news");
        requestBody.put("days", 30);
        requestBody.put("sort_by", "relevancy");
        requestBody.put("max_results", 5);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            "https://api.tavily.com/search",
            HttpMethod.POST,
            request,
            JsonNode.class
        );

        return response.getBody();
    }

    private static final String SYSTEM_PROMPT = """
        You are an assistant that receives a description and your task is to summarize the information to a maximum of 300 characters. 
        The summary must always match the input language—if the input is in Malay or Indonesian, respond in Malay. If the input is in English or any other language, respond in English.
        Do not translate or provide explanations. Do not answer questions—only summarize the content provided.
        """;
}
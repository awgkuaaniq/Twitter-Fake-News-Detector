package veritas.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import veritas.common.model.Feedback;
import veritas.common.model.ManualCheck;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ManualCheckTests {
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void canPerformManualCheck() {
        String query = "Test query for manual check";
        
        ResponseEntity<ManualCheck> response = restTemplate
            .postForEntity("/api/manual-checks?query={query}", 
                         null, 
                         ManualCheck.class, 
                         query);
        
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getQuery()).isEqualTo(query);
        assertThat(response.getBody().getCrosscheck()).isNotNull();
    }

    @Test
    void canAddFeedbackToManualCheck() {
        // First create a manual check
        String query = "Test query for feedback";
        ResponseEntity<ManualCheck> checkResponse = restTemplate
            .postForEntity("/api/manual-checks?query={query}", 
                        null, 
                        ManualCheck.class, 
                        query);
        
        String checkId = checkResponse.getBody().getId();
        
        // Create feedback
        Feedback feedback = new Feedback();
        feedback.setComment("This is helpful");
        feedback.setLike(true);
        
        // Add feedback to the manual check
        ResponseEntity<ManualCheck> feedbackResponse = restTemplate
            .postForEntity("/api/manual-checks/{id}/feedback",
                        feedback,
                        ManualCheck.class,
                        checkId);
        
        // Verify response
        assertThat(feedbackResponse.getStatusCodeValue()).isEqualTo(200);
        assertThat(feedbackResponse.getBody()).isNotNull();
        assertThat(feedbackResponse.getBody().getFeedback()).isNotNull();
        assertThat(feedbackResponse.getBody().getFeedback().getComment())
            .isEqualTo("This is helpful");
        assertThat(feedbackResponse.getBody().getFeedback().getLike())
            .isTrue();
    }
}
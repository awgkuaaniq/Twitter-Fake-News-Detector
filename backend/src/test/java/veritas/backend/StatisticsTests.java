package veritas.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import veritas.backend.repository.TweetRepository;
import veritas.common.model.Tweet;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatisticsTests {
    
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TweetRepository tweetRepository;

    @BeforeEach
    void setUp() {
        tweetRepository.deleteAll();
    }

    @Test
    void canGetMostActiveUsers() {
        // Create test tweets with different users
        Tweet tweet1 = new Tweet();
        tweet1.setUsername("user1");
        tweet1.setBody("Test tweet 1");
        tweet1.setPublishedAt(LocalDateTime.now());

        Tweet tweet2 = new Tweet();
        tweet2.setUsername("user1");
        tweet2.setBody("Test tweet 2");
        tweet2.setPublishedAt(LocalDateTime.now());

        Tweet tweet3 = new Tweet();
        tweet3.setUsername("user2");
        tweet3.setBody("Test tweet 3");
        tweet3.setPublishedAt(LocalDateTime.now());

        // Save tweets
        restTemplate.postForEntity("/api/tweets", tweet1, Tweet.class);
        restTemplate.postForEntity("/api/tweets", tweet2, Tweet.class);
        restTemplate.postForEntity("/api/tweets", tweet3, Tweet.class);

        // Get most active users
        ResponseEntity<Map<String, Long>> response = restTemplate.exchange(
            "/api/tweets/statistics/most-active-users",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Long>>() {}
        );

        // Verify response
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("user1")).isEqualTo(2L);
        assertThat(response.getBody().get("user2")).isEqualTo(1L);
    }

    @Test
    void canGetTodayTweetsCount() {
        // Create tweets for today
        Tweet tweet1 = new Tweet();
        tweet1.setBody("Today's tweet 1");
        tweet1.setPublishedAt(LocalDateTime.now());

        Tweet tweet2 = new Tweet();
        tweet2.setBody("Today's tweet 2");
        tweet2.setPublishedAt(LocalDateTime.now());

        // Create tweet for yesterday
        Tweet tweet3 = new Tweet();
        tweet3.setBody("Yesterday's tweet");
        tweet3.setPublishedAt(LocalDateTime.now().minusDays(1));

        // Save tweets
        restTemplate.postForEntity("/api/tweets", tweet1, Tweet.class);
        restTemplate.postForEntity("/api/tweets", tweet2, Tweet.class);
        restTemplate.postForEntity("/api/tweets", tweet3, Tweet.class);

        // Get today's tweet count
        ResponseEntity<Long> response = restTemplate.getForEntity(
            "/api/tweets/statistics/today-count",
            Long.class
        );

        // Verify response
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(2L);
    }
}
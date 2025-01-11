package veritas.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import veritas.backend.repository.TweetRepository;
import veritas.common.model.CrosscheckResult;
import veritas.common.model.Tweet;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BackendApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

	@Autowired
    private TweetRepository tweetRepository;  // Add this field

    @BeforeEach
    void setUp() {
        tweetRepository.deleteAll();  // Cleans database before each test
    }

    @Test
    void contextLoads() {
    }

    @Test
    void canCreateAndRetrieveTweet() {
        Tweet tweet = new Tweet();
        tweet.setBody("Test tweet");
        
        ResponseEntity<Tweet> createResponse = restTemplate
            .postForEntity("/api/tweets", tweet, Tweet.class);
        assertThat(createResponse.getStatusCodeValue()).isEqualTo(200);
        assertThat(createResponse.getBody().getId()).isNotNull();
        
        ResponseEntity<Tweet[]> getResponse = restTemplate
            .getForEntity("/api/tweets", Tweet[].class);
        assertThat(getResponse.getStatusCodeValue()).isEqualTo(200);
        assertThat(getResponse.getBody()).isNotEmpty();
    }

	@Test
	void canSearchTweets() {
		// Create test tweets
		Tweet tweet1 = new Tweet();
		tweet1.setBody("Donald Trump makes a statement");
		
		Tweet tweet2 = new Tweet();
		tweet2.setBody("Biden responds to policy");
		
		// Save test tweets
		restTemplate.postForEntity("/api/tweets", tweet1, Tweet.class);
		restTemplate.postForEntity("/api/tweets", tweet2, Tweet.class);
		
		// Search for tweets containing "donald"
		ResponseEntity<Tweet[]> searchResponse = restTemplate
			.getForEntity("/api/tweets/search?query=donald", Tweet[].class);
		
		// Assert search results
		assertThat(searchResponse.getStatusCodeValue()).isEqualTo(200);
		assertThat(searchResponse.getBody()).isNotNull();
		assertThat(searchResponse.getBody().length).isEqualTo(1);
		assertThat(searchResponse.getBody()[0].getBody()).contains("Donald");
	}

	@Test
	void canScrapeAndCrosscheckMultipleTweets() {
		// Request tweet scraping with crosscheck
		ResponseEntity<List<Tweet>> response = restTemplate
			.exchange(
				"/api/tweets/scrape-and-crosscheck?count=1",
				HttpMethod.POST,
				null,
				new ParameterizedTypeReference<List<Tweet>>() {}
			);
		
		// Verify response
		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().size()).isGreaterThan(0);
		
		// Verify each tweet has crosscheck results
		response.getBody().forEach(tweet -> {
			assertThat(tweet.getId()).isNotNull();
			assertThat(tweet.getCrosscheck()).isNotNull();
			assertThat(tweet.getCrosscheck().getTitle()).isNotNull();
			assertThat(tweet.getCrosscheck().getContent()).isNotNull();
			assertThat(tweet.getCrosscheck().getSource()).isNotNull();
			assertThat(tweet.getCrosscheck().getProbability()).isBetween(0.0, 1.0);
		});
	}
}
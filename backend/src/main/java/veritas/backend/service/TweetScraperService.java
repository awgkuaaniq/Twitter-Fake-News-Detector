package veritas.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponentsBuilder;
import veritas.common.model.Tweet;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class TweetScraperService {
    @Autowired
    private RestTemplate restTemplate;

    private static final String RAPID_API_KEY = "18b9867409mshbf3384ba0b87c18p1e6e1fjsn7dfa3e09cc4b";
    private static final String API_URL = "https://twitter-api45.p.rapidapi.com/search.php";

    public List<Tweet> scrapeTweets(int loopCount) {
        String nextToken = "";
        List<Tweet> filteredTweets = new ArrayList<>();

        for (int i = 0; i < loopCount; i++) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-rapidapi-key", RAPID_API_KEY);
            headers.set("x-rapidapi-host", "twitter-api45.p.rapidapi.com");

            String urlTemplate = UriComponentsBuilder.fromUriString(API_URL)
                .queryParam("query", "breaking news lang:en")
                .queryParam("search_type", "Top")
                .queryParam("cursor", nextToken)
                .build()
                .toUriString();

            HttpEntity<?> entity = new HttpEntity<>(headers);

            try {
                JsonNode response = restTemplate.exchange(
                    urlTemplate, 
                    HttpMethod.GET, 
                    entity, 
                    JsonNode.class
                ).getBody();

                if (response != null) {
                    nextToken = response.get("next_cursor").asText();
                    JsonNode tweets = response.get("timeline");

                    for (JsonNode tweetNode : tweets) {
                        String articleUrl = null;
                        if (tweetNode.has("entities") && 
                            tweetNode.get("entities").has("urls") && 
                            tweetNode.get("entities").get("urls").size() > 0) {
                            articleUrl = tweetNode.get("entities")
                                .get("urls")
                                .get(0)
                                .get("expanded_url")
                                .asText();
                        }

                        Tweet tweet = new Tweet();
                        tweet.setId(tweetNode.get("tweet_id").asText());
                        tweet.setName(tweetNode.get("user_info").get("name").asText());
                        tweet.setUsername(tweetNode.get("screen_name").asText());
                        tweet.setAvatar(tweetNode.get("user_info").get("avatar").asText());
                        tweet.setBody(tweetNode.get("text").asText());
                        tweet.setTweetUrl("https://x.com/" + 
                            tweetNode.get("screen_name").asText() + 
                            "/status/" + 
                            tweetNode.get("tweet_id").asText());
                        tweet.setArticleUrl(articleUrl);
                        tweet.setPublishedAt(LocalDateTime.parse(
                            tweetNode.get("created_at").asText(),
                            DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss +0000 yyyy")
                        ));

                        filteredTweets.add(tweet);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

        return filteredTweets;
    }
}
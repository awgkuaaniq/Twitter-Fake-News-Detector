package veritas.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import veritas.common.model.CrosscheckResult;
import veritas.common.model.Tweet;
import veritas.backend.service.CrosscheckService;
import veritas.backend.service.TweetScraperService;
import veritas.backend.service.TweetService;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tweets")
@CrossOrigin(origins = "*")
public class TweetController {
    @Autowired
    private TweetService tweetService;
    @Autowired
    private TweetScraperService tweetScraperService;
    @Autowired
    private CrosscheckService crosscheckService;

    @GetMapping
    public ResponseEntity<List<Tweet>> getAllTweets() {
        return ResponseEntity.ok(tweetService.getAllTweets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tweet> getTweetById(@PathVariable String id) {
        return tweetService.getTweetById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tweet> createTweet(@RequestBody Tweet tweet) {
        return ResponseEntity.ok(tweetService.saveTweet(tweet));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable String id) {
        tweetService.deleteTweet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Tweet>> searchTweets(@RequestParam String query) {
        return ResponseEntity.ok(tweetService.searchTweets(query));
    }

    @PostMapping("/scrape")
    public ResponseEntity<List<Tweet>> scrapeTweets(@RequestParam(defaultValue = "3") int count) {
        List<Tweet> tweets = tweetScraperService.scrapeTweets(count);
        return ResponseEntity.ok(tweets);
    }

    @PostMapping("/{id}/crosscheck")
    public ResponseEntity<CrosscheckResult> crosscheckTweet(@PathVariable String id) {
        Optional<Tweet> tweetOpt = tweetService.getTweetById(id);
        if (tweetOpt.isPresent()) {
            Tweet tweet = tweetOpt.get();
            CrosscheckResult result = crosscheckService.crosscheckContent(tweet.getBody());
            if (result != null) {
                tweet.setCrosscheck(result);
                tweetService.saveTweet(tweet);  // Save the updated tweet with crosscheck result
            }
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/scrape-and-crosscheck")
    public ResponseEntity<List<Tweet>> scrapeAndCrosscheckTweets(@RequestParam(defaultValue = "3") int count) {
        List<Tweet> tweets = tweetScraperService.scrapeTweets(count);
        tweets.forEach(tweet -> {
            CrosscheckResult result = crosscheckService.crosscheckContent(tweet.getBody());
            tweet.setCrosscheck(result);
            tweetService.saveTweet(tweet);
        });
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/statistics/most-active-users")
    public ResponseEntity<Map<String, Long>> getMostActiveUsers() {
        return ResponseEntity.ok(tweetService.getMostActiveUsers());
    }

    @GetMapping("/statistics/today-count")
    public ResponseEntity<Long> getTodayTweetsCount() {
        return ResponseEntity.ok(tweetService.getTweetsCountForToday());
    }
}
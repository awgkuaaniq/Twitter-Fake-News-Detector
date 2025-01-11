package veritas.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import veritas.common.model.Tweet;
import veritas.backend.repository.TweetRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class TweetService {
    @Autowired
    private TweetRepository tweetRepository;

    public List<Tweet> getAllTweets() {
        return tweetRepository.findAll();
    }

    public Optional<Tweet> getTweetById(String id) {
        return tweetRepository.findById(id);
    }

    public Tweet saveTweet(Tweet tweet) {
        if (tweet.getPublishedAt() == null) {
            tweet.setPublishedAt(LocalDateTime.now());
        }
        return tweetRepository.save(tweet);
    }

    public void deleteTweet(String id) {
        tweetRepository.deleteById(id);
    }

    public List<Tweet> searchTweets(String query) {
        return tweetRepository.searchTweets(query);
    }

    public Map<String, Long> getMostActiveUsers() {
        List<Tweet> tweets = tweetRepository.findAllUsernames();
        
        // Count occurrences of each username
        Map<String, Long> userCounts = tweets.stream()
            .map(Tweet::getUsername)
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
            ));
        
        // Sort by count in descending order
        return userCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }

    public long getTweetsCountForToday() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return tweetRepository.findByPublishedAtBetween(startOfDay, endOfDay).size();
    }
}
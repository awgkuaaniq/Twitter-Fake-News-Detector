package veritas.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import veritas.common.model.Tweet;

import java.time.LocalDateTime;
import java.util.List;

public interface TweetRepository extends MongoRepository<Tweet, String> {
    @Query("{ '$text': { '$search': ?0 } }")
    List<Tweet> searchTweets(String searchText);
    
    // Find tweets within a date range
    List<Tweet> findByPublishedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Count tweets by username
    @Query(value = "{ 'username': ?0 }")
    long countByUsername(String username);
    
    // Get most active users
    @Query(value = "{}", fields = "{ 'username': 1 }")
    List<Tweet> findAllUsernames();
}
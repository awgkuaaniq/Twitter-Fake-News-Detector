package veritas.common.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Document(collection = "tweet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tweet {
    @Id
    private String id;
    private String name;
    private String username;
    private String avatar;
    private String body;
    private String tweetUrl;
    private String articleUrl;
    private LocalDateTime publishedAt;
    private CrosscheckResult crosscheck;
}
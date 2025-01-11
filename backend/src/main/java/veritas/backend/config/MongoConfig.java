package veritas.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import jakarta.annotation.PostConstruct;

@Configuration
public class MongoConfig {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        TextIndexDefinition textIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
            .onField("body")
            .onField("name")
            .onField("username")
            .build();
        
        mongoTemplate.indexOps("tweet").ensureIndex(textIndex);
    }
}
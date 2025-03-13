package stegochat.stegochat.security;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;

import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    private MongoClient mongoClient;

    @Bean
    public MongoClient mongoClient() {
        this.mongoClient = MongoClients.create(mongoUri);
        return this.mongoClient;
    }

    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(databaseName);
    }

    @Bean
    public GridFSBucket gridFSBucket(MongoDatabase database) {
        return GridFSBuckets.create(database);
    }

    @PreDestroy
    public void closeMongoClient() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}

package ru.seranov.bookexpert.backend.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.NonNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(
        reactiveMongoTemplateRef = "mongodbBooksTemplate",
        basePackages = {"ru.seranov.bookexpert.backend.repository.mongodb_books"})
@EnableConfigurationProperties
public class MongoDbBooksReactiveStreamsConfiguration extends AbstractReactiveMongoConfiguration {
    @NonNull
    final AppConfigProperties properties;

    public MongoDbBooksReactiveStreamsConfiguration(@NonNull final AppConfigProperties properties) {
        this.properties = properties;
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    @Override
    protected String getDatabaseName() {
        return "books";
    }
}

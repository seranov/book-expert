package ru.seranov.bookexpert.backend.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import ru.seranov.bookexpert.backend.repository.mongodb_books.UserRepositoryMongodb;

@Configuration
@EnableReactiveMongoRepositories(
        reactiveMongoTemplateRef = "mongodbBooksTemplate",
        basePackageClasses = {UserRepositoryMongodb.class})
@EnableConfigurationProperties
public class MongoDbBooksReactiveStreamsConfiguration extends AbstractReactiveMongoConfiguration {
    public static final String DATABASE_NAME = "books";

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
    @NonNull
    protected String getDatabaseName() {
        return DATABASE_NAME;
    }

    @Bean
    @Qualifier("mongodbBooksTemplate")
    @NonNull
    public ReactiveMongoTemplate mongodbBooksTemplate() {
        return new ReactiveMongoTemplate(mongoClient(), DATABASE_NAME);
    }
}

package ru.seranov.bookexpert.backend.service;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.seranov.bookexpert.backend.entity.UserMongodb;
import ru.seranov.bookexpert.backend.repository.mongodb_books.UserRepositoryMongodb;

import java.util.List;

@Service
public class MongoDbBooksPrefill {
    @NonNull
    private final UserRepositoryMongodb userRepositoryMongodb;

    @Autowired
    public MongoDbBooksPrefill(final @NonNull UserRepositoryMongodb userRepositoryMongodb) {
        this.userRepositoryMongodb = userRepositoryMongodb;
    }

    public Flux<UserMongodb> prefillUsers() {
        final List<UserMongodb> users = List.of(
                UserMongodb.builder()
                        .username("Mongo")
                        .password("P@ssw0rd")
                        .roles("USER ADMIN")
                        .build());
        return userRepositoryMongodb.insert(users);
    }
}

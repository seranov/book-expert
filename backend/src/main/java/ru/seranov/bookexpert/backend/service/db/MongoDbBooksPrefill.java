package ru.seranov.bookexpert.backend.service.db;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.seranov.bookexpert.backend.model.entity.UserMongodb;
import ru.seranov.bookexpert.backend.repository.mongodb_books.UserRepositoryMongodb;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MongoDbBooksPrefill {
    @NonNull
    private final UserRepositoryMongodb userRepositoryMongodb;

    @Autowired
    public MongoDbBooksPrefill(final @NonNull UserRepositoryMongodb userRepositoryMongodb) {
        this.userRepositoryMongodb = userRepositoryMongodb;
    }

    public List<UserMongodb> prefillUsers() {
        final List<UserMongodb> result = new ArrayList<>();
        final List<UserMongodb> users = List.of(
                UserMongodb.builder()
                        .username("Mongo")
                        .password("P@ssw0rd")
                        .roles("USER ADMIN")
                        .build());
        for (final UserMongodb user : users) {
            try {
                final Mono<UserMongodb> insertMono = userRepositoryMongodb.insert(user);
                result.add(insertMono.block());
            } catch (final Exception exception) {
                log.info("Не удалось загрузить пользователя {}: {}", user, exception.getMessage());
            }
        }
        return result;
    }
}

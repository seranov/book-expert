package ru.seranov.bookexpert.backend.repository.mongodb_books;

import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import ru.seranov.bookexpert.backend.entity.UserAny;
import ru.seranov.bookexpert.backend.entity.UserMongodb;

public interface UserRepositoryMongodb extends ReactiveMongoRepository<UserMongodb, ObjectId> {
    @NonNull
    Mono<UserAny> findByUsername(@NonNull final String username);
}
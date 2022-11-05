package ru.seranov.bookexpert.backend.repository.postgres_book_expert;

import lombok.NonNull;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import ru.seranov.bookexpert.backend.entity.User;

public interface UserRepository extends R2dbcRepository<User, Long> {
    @Query("SELECT * FROM book_expert.user WHERE username = :username AND password = :password")
    @NonNull Mono<User> findByUsernameAndPassword(@NonNull final String username, @NonNull final String password);

    @Query("SELECT * FROM book_expert.user WHERE username = :username")
    @NonNull Mono<User> findByUsername(@NonNull final String username);
}

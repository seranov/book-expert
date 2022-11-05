package ru.seranov.bookexpert.backend.service.db;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.seranov.bookexpert.backend.model.entity.UserAny;
import ru.seranov.bookexpert.backend.repository.mongodb_books.UserRepositoryMongodb;
import ru.seranov.bookexpert.backend.repository.postgres_book_expert.UserRepositoryPostgresql;
import ru.seranov.bookexpert.core.model.dto.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static ru.seranov.bookexpert.backend.mapper.UserMapper.USER_MAPPER;

@Service
public class UserServiceDb implements UserService {
    @NonNull
    private final UserRepositoryPostgresql userRepositoryPostgresql;

    @NonNull
    private final UserRepositoryMongodb userRepositoryMongodb;

    @Autowired
    public UserServiceDb(final @NonNull UserRepositoryPostgresql userRepositoryPostgresql,
                         final @NonNull UserRepositoryMongodb userRepositoryMongodb) {
        this.userRepositoryPostgresql = userRepositoryPostgresql;
        this.userRepositoryMongodb = userRepositoryMongodb;
    }

    static Optional<UserAny> getBy(@NonNull final List<Mono<UserAny>> entityMonoCollection) {
        final UserAny userAny = Flux.fromIterable(entityMonoCollection)
                .flatMap(mono -> mono)
                .filter(Objects::nonNull)
                .blockFirst();
        return Optional.ofNullable(userAny);
    }

    static Optional<User> getBy(@NonNull final Supplier<List<Mono<UserAny>>> entitySuppliers) {
        final List<Mono<UserAny>> entityMonoCollection = entitySuppliers.get();
        final Optional<UserAny> entity = getBy(entityMonoCollection);
        final User dto = USER_MAPPER.toDto(entity.orElse(null));
        return Optional.ofNullable(dto);
    }

    @Override
    public @NonNull Optional<User> getByUsername(final @NonNull String username) {
        return getBy(() -> List.of(
                userRepositoryPostgresql.findByUsername(username),
                userRepositoryMongodb.findByUsername(username)
        ));
    }
}

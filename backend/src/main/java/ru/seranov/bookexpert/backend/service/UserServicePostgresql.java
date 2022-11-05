package ru.seranov.bookexpert.backend.service;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.seranov.bookexpert.backend.repository.postgres_book_expert.UserRepository;
import ru.seranov.bookexpert.core.dto.User;

import java.util.Optional;
import java.util.function.Supplier;

import static ru.seranov.bookexpert.backend.mapper.UserMapper.USER_MAPPER;

@Service
public class UserServicePostgresql implements UserService {
    @NonNull
    private final UserRepository userRepository;

    @Autowired
    public UserServicePostgresql(@NonNull final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    static Optional<User> getBy(
            @NonNull final Supplier<Mono<ru.seranov.bookexpert.backend.entity.User>> entitySupplier) {
        final ru.seranov.bookexpert.backend.entity.User entity = entitySupplier.get().block();
        final User dto = USER_MAPPER.toDto(entity);
        return Optional.ofNullable(dto);
    }

    @Override
    @NonNull
    public Optional<User> getByCredentials(@NonNull final String username, @NonNull final String password) {
        return getBy(() -> userRepository.findByUsernameAndPassword(username, password));
    }

    @Override
    public @NonNull Optional<User> getByUsername(final @NonNull String username) {
        return getBy(() -> userRepository.findByUsername(username));
    }
}

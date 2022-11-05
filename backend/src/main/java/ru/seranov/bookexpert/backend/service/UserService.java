package ru.seranov.bookexpert.backend.service;

import lombok.NonNull;
import ru.seranov.bookexpert.core.dto.User;

import java.util.Optional;

public interface UserService {
    @NonNull
    Optional<User> getByCredentials(@NonNull final String username, @NonNull final String password);

    @NonNull
    Optional<User> getByUsername(@NonNull final String username);
}

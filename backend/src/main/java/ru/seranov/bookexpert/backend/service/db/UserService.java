package ru.seranov.bookexpert.backend.service.db;

import lombok.NonNull;
import ru.seranov.bookexpert.core.model.dto.User;

import java.util.Optional;

public interface UserService {
    @NonNull
    Optional<User> getByUsername(@NonNull final String username);
}

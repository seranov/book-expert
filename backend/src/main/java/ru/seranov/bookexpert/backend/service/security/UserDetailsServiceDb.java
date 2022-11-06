package ru.seranov.bookexpert.backend.service.security;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.seranov.bookexpert.backend.service.db.UserService;
import ru.seranov.bookexpert.core.model.dto.User;

import java.util.Optional;

import static ru.seranov.bookexpert.backend.mapper.UserMapper.USER_MAPPER;

public class UserDetailsServiceDb implements UserDetailsService {
    @NonNull
    private final UserService userService;

    @NonNull
    private final PasswordEncoder passwordEncoder;

    public UserDetailsServiceDb(final @NonNull UserService userService,
                                final @NonNull PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(@Nullable final String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException(username);
        }
        final @NonNull Optional<User> dtoOptional = userService.getByUsername(username);
        final User dto = dtoOptional.orElseThrow(() -> new UsernameNotFoundException(username));
        return USER_MAPPER.toUserDetails(dto, passwordEncoder);
    }
}

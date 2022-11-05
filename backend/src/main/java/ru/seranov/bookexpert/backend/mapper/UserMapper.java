package ru.seranov.bookexpert.backend.mapper;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.seranov.bookexpert.backend.entity.UserAny;
import ru.seranov.bookexpert.core.dto.User;

@Mapper
public interface UserMapper {
    @NonNull
    UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

    @Nullable
    User toDto(@Nullable final UserAny entity);

    @Nullable
    default UserDetails toUserDetails(@Nullable final User dto, @NonNull final PasswordEncoder passwordEncoder) {
        final UserDetails user;
        if (dto != null) {
            final org.springframework.security.core.userdetails.User.UserBuilder builder
                    = org.springframework.security.core.userdetails.User.withUsername(dto.getUsername());
            builder.password(passwordEncoder.encode(dto.getPassword()));
            final String roles = dto.getRoles();
            if (StringUtils.isNotBlank(roles)) {
                builder.roles(StringUtils.split(roles, " \t,;"));
            }
            user = builder.build();
        } else {
            user = null;
        }
        return user;
    }
}

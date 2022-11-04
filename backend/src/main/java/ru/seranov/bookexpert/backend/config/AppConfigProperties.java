package ru.seranov.bookexpert.backend.config;

import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "app")
@ConstructorBinding
public record AppConfigProperties(String version, AppDatabaseConfigProperties database) {
    @ConstructorBinding
    public record AppDatabaseConfigProperties(R2dbcProperties postgresBookExpert, R2dbcProperties mongoBooks) {
    }
}

package ru.seranov.bookexpert.backend.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;

import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

@Configuration
@EnableR2dbcRepositories(
        entityOperationsRef = "postgresBookExpertEntityTemplate",
        basePackages = {"ru.seranov.bookexpert.backend.repository.postgres_book_expert"})
@EnableConfigurationProperties
public class PostgresBookExpertR2dbcConfiguration {
    @NonNull
    final AppConfigProperties properties;

    public PostgresBookExpertR2dbcConfiguration(@NonNull final AppConfigProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Qualifier(value = "postgresBookExpertConnectionFactory")
    public ConnectionFactory postgresBookExpertConnectionFactory() {
        final R2dbcProperties r2dbcProperties = properties.database().postgresBookExpert();
        final ConnectionFactoryOptions baseOptions = ConnectionFactoryOptions.parse(r2dbcProperties.getUrl());
        ConnectionFactoryOptions.Builder ob = ConnectionFactoryOptions.builder().from(baseOptions);
        if (!StringUtils.isBlank(r2dbcProperties.getUsername())) {
            ob = ob.option(USER, r2dbcProperties.getUsername());
        }
        if (!StringUtils.isBlank(r2dbcProperties.getPassword())) {
            ob = ob.option(PASSWORD, r2dbcProperties.getPassword());
        }
        return ConnectionFactories.get(ob.build());
    }

    @Bean
    public R2dbcEntityOperations postgresBookExpertEntityTemplate(
            @Qualifier("postgresBookExpertConnectionFactory") ConnectionFactory connectionFactory) {
        final DefaultReactiveDataAccessStrategy strategy =
                new DefaultReactiveDataAccessStrategy(PostgresDialect.INSTANCE);
        final DatabaseClient databaseClient = DatabaseClient.builder()
                .connectionFactory(connectionFactory)
                .bindMarkers(PostgresDialect.INSTANCE.getBindMarkersFactory())
                .build();

        return new R2dbcEntityTemplate(databaseClient, strategy);
    }
}
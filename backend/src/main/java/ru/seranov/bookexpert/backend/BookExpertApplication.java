package ru.seranov.bookexpert.backend;

import io.r2dbc.spi.ConnectionFactory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import ru.seranov.bookexpert.backend.config.AppConfigProperties;
import ru.seranov.bookexpert.backend.entity.User;

@Slf4j
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
@ConfigurationPropertiesScan(basePackageClasses = {AppConfigProperties.class})
@EntityScan(basePackageClasses = {User.class})
public class BookExpertApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookExpertApplication.class, args);
    }

    @Autowired
    @Bean
    @Nullable
    ConnectionFactoryInitializer initializer(@NonNull final ConnectionFactory connectionFactory) {
        log.info("Инициализация фабрики подключений {}", connectionFactory);
        return null;
    }
}

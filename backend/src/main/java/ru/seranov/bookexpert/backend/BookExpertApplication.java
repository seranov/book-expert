package ru.seranov.bookexpert.backend;

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
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import ru.seranov.bookexpert.backend.config.AppConfigProperties;
import ru.seranov.bookexpert.backend.model.entity.UserPostgresql;
import ru.seranov.bookexpert.backend.service.db.PrefillDb;

@Slf4j
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
@ConfigurationPropertiesScan(basePackageClasses = {AppConfigProperties.class})
@EntityScan(basePackageClasses = {UserPostgresql.class})
public class BookExpertApplication implements ApplicationListener<ContextRefreshedEvent> {
    @NonNull
    private final PrefillDb prefillDb;

    @Autowired
    public BookExpertApplication(final @NonNull PrefillDb prefillDb) {
        this.prefillDb = prefillDb;
    }

    public static void main(String[] args) {
        SpringApplication.run(BookExpertApplication.class, args);
    }

    @Override
    public void onApplicationEvent(@NonNull final ContextRefreshedEvent event) {
        log.info("Контекст приложениия обновлён");
        prefillDb.prefill();
    }
}

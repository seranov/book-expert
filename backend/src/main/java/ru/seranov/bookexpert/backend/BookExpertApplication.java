package ru.seranov.bookexpert.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan("ru.seranov.bookexpert.backend")
public class BookExpertApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookExpertApplication.class, args);
    }

}

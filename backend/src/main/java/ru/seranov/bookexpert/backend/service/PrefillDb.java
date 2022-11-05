package ru.seranov.bookexpert.backend.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.seranov.bookexpert.backend.entity.UserMongodb;

import java.util.List;

@Service
@Slf4j
public class PrefillDb {
    @NonNull
    private final MongoDbBooksPrefill mongoDbBooksPrefill;

    public PrefillDb(final @NonNull MongoDbBooksPrefill mongoDbBooksPrefill) {
        this.mongoDbBooksPrefill = mongoDbBooksPrefill;
    }

    public void prefill() {
        final Flux<UserMongodb> mongoUsers = mongoDbBooksPrefill.prefillUsers();
        final List<UserMongodb> res = Flux.fromIterable(List.of(mongoUsers))
                .flatMap(users -> users)
                .collectList()
                .block();
        log.info("Записано {} пользователей", res != null ? res.size() : 0);
    }
}

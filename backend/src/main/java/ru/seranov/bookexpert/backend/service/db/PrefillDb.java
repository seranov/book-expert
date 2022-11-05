package ru.seranov.bookexpert.backend.service.db;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.seranov.bookexpert.backend.model.entity.UserMongodb;

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
        final List<UserMongodb> mongoUsers = mongoDbBooksPrefill.prefillUsers();
        log.info("Записано {} пользователей", mongoUsers != null ? mongoUsers.size() : 0);
    }
}

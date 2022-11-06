package ru.seranov.bookexpert.backend.service.book;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.seranov.bookexpert.core.model.grpc.BookDescriptorOuterClass;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Slf4j
public class BookServiceImpl implements BookService {
    static void processAdd(@NonNull final String name) {
        log.info("Добавление книги {}", name);
    }

    static Duration profile(@NonNull final Runnable runnable) {
        final LocalDateTime beginTime = LocalDateTime.now();
        runnable.run();
        return Duration.between(beginTime, LocalDateTime.now());
    }

    static BookDescriptorOuterClass.BookAddResponse makeResponse(@Nullable final String id,
                                                                 @NonNull final Duration duration) {
        return BookDescriptorOuterClass.BookAddResponse.newBuilder()
                .setRequestId(id)
                .setProcessTime(duration.toMillis())
                .build();
    }

    @NonNull
    @Override
    public BookDescriptorOuterClass.BookAddResponse add(
            @NonNull final BookDescriptorOuterClass.BookDescriptor bookDescriptor) {
        final Duration duration = profile(() -> processAdd(bookDescriptor.getName()));
        return makeResponse(bookDescriptor.getId(), duration);
    }
}

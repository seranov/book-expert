package ru.seranov.bookexpert.backend.service.book;

import lombok.NonNull;
import ru.seranov.bookexpert.core.model.dto.BookAddRequest;
import ru.seranov.bookexpert.core.model.dto.BookAddResponse;

public interface BookService {
    @NonNull
    BookAddResponse add(@NonNull final BookAddRequest bookAddRequest);
}

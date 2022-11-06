package ru.seranov.bookexpert.backend.service.book;

import lombok.NonNull;
import ru.seranov.bookexpert.core.model.grpc.BookDescriptorOuterClass;

public interface BookService {
    @NonNull
    BookDescriptorOuterClass.BookAddResponse add(
            @NonNull final BookDescriptorOuterClass.BookDescriptor bookDescriptor);
}

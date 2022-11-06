package ru.seranov.bookexpert.backend.mapper;

import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.lang.Nullable;
import ru.seranov.bookexpert.backend.model.rest.BookAddRestRequest;
import ru.seranov.bookexpert.backend.model.websocket.BookAddWebsocketRequest;
import ru.seranov.bookexpert.core.model.dto.BookAddRequest;
import ru.seranov.bookexpert.core.model.grpc.BookDescriptorOuterClass;

@Mapper
public interface BookAddRequestMapper {
    @NonNull
    BookAddRequestMapper BOOK_ADD_REQUEST_MAPPER = Mappers.getMapper(BookAddRequestMapper.class);

    @Nullable
    BookAddRequest toDto(@Nullable final BookDescriptorOuterClass.BookDescriptor request);

    @Nullable
    BookAddRequest toDto(@Nullable final BookAddRestRequest request);

    @Nullable
    BookAddRequest toDto(@Nullable final BookAddWebsocketRequest request);
}

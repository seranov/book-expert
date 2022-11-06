package ru.seranov.bookexpert.backend.mapper;

import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.lang.Nullable;
import ru.seranov.bookexpert.backend.model.rest.BookAddRestResponse;
import ru.seranov.bookexpert.backend.model.websocket.BookAddWebsocketResponse;
import ru.seranov.bookexpert.core.model.dto.BookAddResponse;
import ru.seranov.bookexpert.core.model.grpc.BookDescriptorOuterClass;

@Mapper
public interface BookAddResponseMapper {
    @NonNull
    BookAddResponseMapper BOOK_ADD_RESPONSE_REQUEST_MAPPER = Mappers.getMapper(BookAddResponseMapper.class);

    @Nullable
    BookDescriptorOuterClass.BookAddResponse toGrpc(@Nullable final BookAddResponse bookAddResponse);

    @Nullable
    BookAddRestResponse toRest(@Nullable final BookAddResponse bookAddResponse);

    @Nullable
    BookAddWebsocketResponse toWebsocket(@Nullable final BookAddResponse bookAddResponse);
}

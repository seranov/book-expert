package ru.seranov.bookexpert.backend.controller.websocket;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ru.seranov.bookexpert.backend.model.websocket.BookAddWebsocketRequest;
import ru.seranov.bookexpert.backend.model.websocket.BookAddWebsocketResponse;
import ru.seranov.bookexpert.backend.service.book.BookService;
import ru.seranov.bookexpert.core.model.dto.BookAddRequest;
import ru.seranov.bookexpert.core.model.dto.BookAddResponse;

import static ru.seranov.bookexpert.backend.mapper.BookAddRequestMapper.BOOK_ADD_REQUEST_MAPPER;
import static ru.seranov.bookexpert.backend.mapper.BookAddResponseMapper.BOOK_ADD_RESPONSE_REQUEST_MAPPER;

@Controller
public class BookServiceWebsocketController {
    @NonNull
    private final BookService bookService;

    @Autowired
    public BookServiceWebsocketController(@NonNull final BookService bookService) {
        this.bookService = bookService;
    }

    @MessageMapping("/add")
    @SendTo("/topic/book_add_requested")
    @NonNull
    public BookAddWebsocketResponse greeting(@NonNull final BookAddWebsocketRequest request) throws Exception {
        Thread.sleep(1000); // simulated delay
        final BookAddRequest bookAddRequest = BOOK_ADD_REQUEST_MAPPER.toDto(request);
        final @NonNull BookAddResponse response = bookService.add(bookAddRequest);
        final BookAddWebsocketResponse responseWebsocket = BOOK_ADD_RESPONSE_REQUEST_MAPPER.toWebsocket(response);
        return responseWebsocket;
    }
}

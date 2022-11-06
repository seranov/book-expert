package ru.seranov.bookexpert.backend.controller.rest;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.seranov.bookexpert.backend.model.rest.BookAddRestRequest;
import ru.seranov.bookexpert.backend.model.rest.BookAddRestResponse;
import ru.seranov.bookexpert.backend.service.book.BookService;
import ru.seranov.bookexpert.core.model.dto.BookAddRequest;
import ru.seranov.bookexpert.core.model.dto.BookAddResponse;

import static ru.seranov.bookexpert.backend.mapper.BookAddRequestMapper.BOOK_ADD_REQUEST_MAPPER;
import static ru.seranov.bookexpert.backend.mapper.BookAddResponseMapper.BOOK_ADD_RESPONSE_REQUEST_MAPPER;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class BookServiceRestController {
    @NonNull
    private final BookService bookService;

    @Autowired
    public BookServiceRestController(@NonNull final BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(path = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello!");
    }


    @PostMapping(path = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookAddRestResponse> add(@RequestBody @NonNull final BookAddRestRequest request) {
        final BookAddRequest dto = BOOK_ADD_REQUEST_MAPPER.toDto(request);
        final BookAddResponse response = bookService.add(dto);
        final BookAddRestResponse responseRest = BOOK_ADD_RESPONSE_REQUEST_MAPPER.toRest(response);
        return ResponseEntity.ok(responseRest);
    }
}

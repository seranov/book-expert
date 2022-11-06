package ru.seranov.bookexpert.backend.controller.rest;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.seranov.bookexpert.backend.service.book.BookService;
import ru.seranov.bookexpert.core.model.grpc.BookDescriptorOuterClass;

@RestController
@RequestMapping("/api")
@Slf4j
public class BookServiceRestController {
    @NonNull
    private final BookService bookService;

    @Autowired
    public BookServiceRestController(@NonNull final BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping(path = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello!");
    }


    @PostMapping(path = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> ping(@RequestBody @NonNull final BookDescriptorOuterClass.BookDescriptor request) {
        return ResponseEntity.ok(bookService.add(request));
    }
}

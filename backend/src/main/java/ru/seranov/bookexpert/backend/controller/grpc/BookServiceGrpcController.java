package ru.seranov.bookexpert.backend.controller.grpc;

import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import ru.seranov.bookexpert.backend.aop.LogMethodCall;
import ru.seranov.bookexpert.backend.service.book.BookService;
import ru.seranov.bookexpert.core.model.dto.BookAddRequest;
import ru.seranov.bookexpert.core.model.dto.BookAddResponse;
import ru.seranov.bookexpert.core.model.grpc.BookDescriptorOuterClass;
import ru.seranov.bookexpert.core.service.grpc.BookServiceGrpc;

import static ru.seranov.bookexpert.backend.mapper.BookAddRequestMapper.BOOK_ADD_REQUEST_MAPPER;
import static ru.seranov.bookexpert.backend.mapper.BookAddResponseMapper.BOOK_ADD_RESPONSE_REQUEST_MAPPER;

@GRpcService
@Slf4j
public class BookServiceGrpcController extends BookServiceGrpc.BookServiceImplBase {
    @NonNull
    private final BookService bookService;

    @Autowired
    public BookServiceGrpcController(@NonNull final BookService bookService) {
        this.bookService = bookService;
    }

    @LogMethodCall
    @Override
    public void add(@NonNull final BookDescriptorOuterClass.BookDescriptor request,
                    @NonNull final StreamObserver<BookDescriptorOuterClass.BookAddResponse> responseObserver) {
        final BookAddRequest bookAddRequest = BOOK_ADD_REQUEST_MAPPER.toDto(request);
        final @NonNull BookAddResponse response = bookService.add(bookAddRequest);
        final BookDescriptorOuterClass.BookAddResponse responseGrpc
                = BOOK_ADD_RESPONSE_REQUEST_MAPPER.toGrpc(response);
        responseObserver.onNext(responseGrpc);
        responseObserver.onCompleted();
    }
}

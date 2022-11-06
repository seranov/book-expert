package ru.seranov.bookexpert.backend.controller.grpc;

import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import ru.seranov.bookexpert.backend.service.book.BookService;
import ru.seranov.bookexpert.core.model.grpc.BookDescriptorOuterClass;
import ru.seranov.bookexpert.core.service.grpc.BookServiceGrpc;

@GRpcService
@Slf4j
public class BookServiceGrpcController extends BookServiceGrpc.BookServiceImplBase {
    @NonNull
    private final BookService bookService;

    @Autowired
    public BookServiceGrpcController(@NonNull final BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public void add(@NonNull final BookDescriptorOuterClass.BookDescriptor request,
                    @NonNull final StreamObserver<BookDescriptorOuterClass.BookAddResponse> responseObserver) {
        final BookDescriptorOuterClass.BookAddResponse response = bookService.add(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

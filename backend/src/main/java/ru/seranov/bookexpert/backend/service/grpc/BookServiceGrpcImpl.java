package ru.seranov.bookexpert.backend.service.grpc;

import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import ru.seranov.bookexpert.core.model.grpc.BookDescriptorOuterClass;
import ru.seranov.bookexpert.core.service.grpc.BookServiceGrpc;

@GRpcService
@Slf4j
public class BookServiceGrpcImpl extends BookServiceGrpc.BookServiceImplBase {
    @Override
    public void add(@NonNull final BookDescriptorOuterClass.BookDescriptor request,
                    @NonNull final StreamObserver<BookDescriptorOuterClass.BookDescriptor> responseObserver) {
        log.info("add book {}", request);
        responseObserver.onNext(BookDescriptorOuterClass.BookDescriptor.newBuilder()
                .setName(request.getName())
                .setDescription("Эхо: " + request.getDescription())
                .build());
        responseObserver.onCompleted();
    }
}

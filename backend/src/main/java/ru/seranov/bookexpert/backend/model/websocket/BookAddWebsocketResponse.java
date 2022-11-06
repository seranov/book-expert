package ru.seranov.bookexpert.backend.model.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookAddWebsocketResponse {
    private String requestId;
    private long processTime;
}

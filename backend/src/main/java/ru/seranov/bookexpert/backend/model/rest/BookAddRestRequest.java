package ru.seranov.bookexpert.backend.model.rest;

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
public class BookAddRestRequest {
    private String id;
    private String name;
    private String description;
}

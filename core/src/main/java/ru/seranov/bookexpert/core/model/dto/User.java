package ru.seranov.bookexpert.core.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class User {
    private String username;

    private String password;

    private String roles;
}

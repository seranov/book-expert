package ru.seranov.bookexpert.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import javax.persistence.Table;
import java.util.UUID;

@Table
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPostgresql implements UserAny {
    @Id
    private UUID id;

    private String username;

    private String password;

    private String roles;
}

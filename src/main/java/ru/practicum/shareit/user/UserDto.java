package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {
    private Integer id;
    @NotNull
    private String name;
    @Email
    @NotNull
    private String email;
}

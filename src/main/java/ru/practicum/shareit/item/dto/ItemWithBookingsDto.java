package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ItemWithBookingsDto {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Integer owner;
    @NotNull
    private Boolean available;
    private List<CommentDto> comments;
    private LocalDate lastBooking;
    private LocalDate nextBooking;
}

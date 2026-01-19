package ru.practicum.shareit.request.dto;

import lombok.Data;

@Data
public class ItemResponseDto {
    private Integer id;
    private Integer userId;
    private Integer itemId;
    private String name;
}

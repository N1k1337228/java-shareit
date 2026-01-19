package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Data
public class ItemResponseDto {
    private Integer id;
    private Integer userId;
    private Integer itemId;
    private String name;
}

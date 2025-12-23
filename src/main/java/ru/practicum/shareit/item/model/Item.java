package ru.practicum.shareit.item.model;

import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Integer id;
    private String name;
    private String description;
    private Integer owner;
    private Boolean available;
}

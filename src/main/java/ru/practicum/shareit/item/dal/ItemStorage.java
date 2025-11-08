package ru.practicum.shareit.item.dal;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Optional<Item> getItem(int itemId);

    void addItem(ItemDto item);

    void updateItem(ItemDto itemDto, int itemId);

    List<Item> getItemsListOnUsersId(Integer userId);

    List<Item> searchItem(String text);


}

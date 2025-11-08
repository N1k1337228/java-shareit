package ru.practicum.shareit.item.dal;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Optional<Item> getItem(int itemId);

    Item addItem(ItemDto item, int userId);

    Item updateItem(Item item, int itemId);

    List<Item> getItemsListOnUsersId(Integer userId);

    List<Item> searchItem(String text);


}

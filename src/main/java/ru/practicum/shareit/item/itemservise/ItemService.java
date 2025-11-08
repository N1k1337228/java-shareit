package ru.practicum.shareit.item.itemservise;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getItem(int itemId, int userId);

    void addItem(ItemDto item, int userId);

    void updateItem(ItemDto itemDto, int itemId, int userId);

    List<ItemDto> getItemsListOnUsersId(int userId);

    List<ItemDto> searchItem(String text);
}

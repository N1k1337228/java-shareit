package ru.practicum.shareit.item.itemservise;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
    ItemWithBookingsDto getItem(int itemId, int userId);

    ItemDto addItem(ItemDto item, int userId);

    ItemDto updateItem(ItemDto itemDto, int itemId, int userId);

    List<ItemWithBookingsDto> getItemsListOnUsersId(int ownerId);

    List<ItemDto> searchItem(String text);

    CommentDto addComment(int itemId, int userId, CommentDto commentDto);
}
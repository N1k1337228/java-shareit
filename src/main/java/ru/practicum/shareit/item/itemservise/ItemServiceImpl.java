package ru.practicum.shareit.item.itemservise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemRepository;
    private final UserStorage userRepository;
    private final ItemMapper itemMapper;

    public ItemDto getItem(int itemId, int userId) {
        userRepository.getUserOnId(userId).orElseThrow(() ->
                new NotFoundException("Незарегистрированный пользователь не может запрашивать вещи"));
        Item item = itemRepository.getItem(itemId).orElseThrow(() -> new NotFoundException("Пользователь с id "
                + userId + " не найден"));
        return itemMapper.toItemDto(item);
    }

    public ItemDto addItem(ItemDto item, int userId) {
        userRepository.getUserOnId(userId).orElseThrow(() ->
                new NotFoundException("Незарегистрированный пользователь не может добавлять вещи"));
        return itemMapper.toItemDto(itemRepository.addItem(item, userId));
    }

    public ItemDto updateItem(ItemDto itemDto, int itemId, int userId) {
        userRepository.getUserOnId(userId).orElseThrow(() ->
                new NotFoundException("Незарегистрированный пользователь не может обновлять вещи"));
        Item oldItem = itemRepository.getItem(itemId).orElseThrow(() -> new NotFoundException("Вещь с id "
                + itemId + "не найдена"));
        if (!oldItem.getOwner().equals(userId)) {
            log.error("Попытка обновить вещь с id {} не её владельцем", itemId);
            throw new ValidationException("Обновлять вещь может только её владелец");
        }
        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toItemDto(itemRepository.updateItem(oldItem, itemId));
    }

    public List<ItemDto> getItemsListOnUsersId(int userId) {
        userRepository.getUserOnId(userId).orElseThrow(() ->
                new NotFoundException("Нельзя запросить список вещей у незарегистрированного пользователя"));
        return itemRepository.getItemsListOnUsersId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItem(String text) {
        return itemRepository.searchItem(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}

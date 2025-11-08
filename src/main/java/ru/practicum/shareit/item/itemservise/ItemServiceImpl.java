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
        Item item = itemRepository.getItem(itemId).orElseThrow(() -> new NotFoundException(""));
        return itemMapper.toItemDto(item);
    }

    public void addItem(ItemDto item, int userId) {
        userRepository.getUserOnId(userId).orElseThrow(() ->
                new ValidationException("Незарегистрированный пользователь не может добавлять вещи"));
        itemRepository.addItem(item);
    }

    public void updateItem(ItemDto itemDto, int itemId, int userId) {
        userRepository.getUserOnId(userId).orElseThrow(() ->
                new ValidationException("Незарегистрированный пользователь не может обновлять вещи"));
        Item item = itemRepository.getItem(itemId).orElseThrow(() -> new NotFoundException(""));
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
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}

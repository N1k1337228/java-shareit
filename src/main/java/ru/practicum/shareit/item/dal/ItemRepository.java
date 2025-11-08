package ru.practicum.shareit.item.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepository implements ItemStorage {
    private final ItemMapper itemMapper;
    public Integer idCount = 0;
    private final HashMap<Integer, Item> itemStorage = new HashMap<>();

    public Optional<Item> getItem(int itemId) {
        Item item = itemStorage.get(itemId);
        if (item == null) {
            return Optional.empty();
        }
        return Optional.of(item);
    }

    public void addItem(ItemDto itemDto) {
        Item item = itemMapper.fromItemDto(itemDto);
        item.setId(generateId());
        itemStorage.put(item.getId(), item);
    }

    public void updateItem(ItemDto itemDto, int itemId) {
        Item item = itemStorage.get(itemId);
        if (itemDto.getName() != null && itemDto.getDescription() != null && itemDto.getAvailable() != null) {
            item.setName(itemDto.getName());
            item.setDescription(itemDto.getDescription());
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getName() != null && itemDto.getDescription() == null && itemDto.getAvailable() == null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getName() == null && itemDto.getDescription() != null && itemDto.getAvailable() == null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getName() == null && itemDto.getDescription() == null && itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
    }

    public void deleteItem(int id) {
        itemStorage.remove(id);
    }

    public List<Item> getItemsListOnUsersId(Integer userId) {
        return itemStorage.values().stream()
                .filter(item -> item.getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Item> searchItem(String text) {
        return itemStorage.values().stream()
                .filter(item -> item.getName().contains(text) || item.getDescription().contains(text))
                .collect(Collectors.toList());

    }

    private Integer generateId() {
        idCount++;
        return idCount;
    }
}

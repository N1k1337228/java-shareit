package ru.practicum.shareit.item.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepository implements ItemStorage {
    private final ItemMapper itemMapper;
    private final HashMap<Integer, Item> itemStorage = new HashMap<>();
    public Integer idCount = 0;

    public Optional<Item> getItem(int itemId) {
        Item item = itemStorage.get(itemId);
        if (item == null) {
            return Optional.empty();
        }
        return Optional.of(item);
    }

    public Item addItem(ItemDto itemDto, int userId) {
        Item item = itemMapper.fromItemDto(itemDto);
        item.setId(generateId());
        item.setOwner(userId);
        itemStorage.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Item item, int itemId) {
        Item item1 = itemStorage.get(itemId);
        if (item.getName() != null && item.getDescription() != null && item.getAvailable() != null) {
            item1.setName(item.getName());
            item1.setDescription(item.getDescription());
            item1.setAvailable(item.getAvailable());
        }
        if (item.getName() != null && item.getDescription() == null && item.getAvailable() == null) {
            item1.setName(item.getName());
        }
        if (item.getName() == null && item.getDescription() != null && item.getAvailable() == null) {
            item1.setDescription(item.getDescription());
        }
        if (item.getName() == null && item.getDescription() == null && item.getAvailable() != null) {
            item1.setAvailable(item.getAvailable());
        }
        return item1;
    }

    public void deleteItem(int id) {
        itemStorage.remove(id);
    }

    public List<Item> getItemsListOnUsersId(Integer userId) {
        return itemStorage.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Item> searchItem(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        String search = text.toLowerCase();
        return itemStorage.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(search) ||
                        item.getDescription().toLowerCase().contains(search))
                .collect(Collectors.toList());

    }

    private Integer generateId() {
        idCount++;
        return idCount;
    }
}

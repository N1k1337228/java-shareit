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
    private Integer idCount = 0;

    public Optional<Item> getItem(int itemId) {
        return Optional.ofNullable(itemStorage.get(itemId));
    }

    public Item addItem(ItemDto itemDto, int userId) {
        Item item = itemMapper.fromItemDto(itemDto);
        item.setId(generateId());
        item.setOwner(userId);
        itemStorage.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Item item, int itemId) {
        itemStorage.put(itemId, item);
        return item;
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
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(search) ||
                        item.getDescription().toLowerCase().contains(search))
                .collect(Collectors.toList());

    }

    private Integer generateId() {
        idCount++;
        return idCount;
    }
}

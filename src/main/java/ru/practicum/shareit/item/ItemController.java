package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemservise.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable("itemId")  @Positive  @NotNull  Integer id,
                           @RequestHeader("X-Sharer-User-Id")  @Positive @NotNull Integer userId) {
        return itemService.getItem(id, userId);
    }

    @PostMapping
    public void addItem(@RequestBody @NotNull @Valid ItemDto itemDto,
                        @RequestHeader("X-Sharer-User-Id") @NotNull @Positive Integer id) {
        itemService.addItem(itemDto, id);
    }

    @PatchMapping
    public void updateItem(@RequestBody @NotNull ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") @NotNull @Positive Integer userId,
                           @PathVariable("itemId") @NotNull @Positive Integer id) {
        itemService.updateItem(itemDto, id, userId);
    }

    @GetMapping
    public List<ItemDto> getUsersItems(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Integer userId) {
        return itemService.getItemsListOnUsersId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String requestText) {
        return itemService.searchItem(requestText);

    }
}

package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
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

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") @Positive @NotNull Integer userId,
                                 @RequestBody @Valid CommentDto commentDto, @PathVariable @Positive @NotNull Integer itemId) {
        return itemService.addComment(itemId, userId, commentDto);
    }

    @PostMapping
    public ItemDto addItem(@RequestBody @NotNull @Valid ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") @Positive Integer id) {
        return itemService.addItem(itemDto, id);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@RequestBody @NotNull ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") @NotNull @Positive Integer userId,
                              @PathVariable("itemId") @NotNull @Positive Integer id) {
        return itemService.updateItem(itemDto, id, userId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getUsersItems(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Integer userId) {
        return itemService.getItemsListOnUsersId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getItem(@PathVariable("itemId") @Positive @NotNull Integer id,
                                       @RequestHeader("X-Sharer-User-Id") @Positive @NotNull Integer userId) {
        return itemService.getItem(id, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String requestText) {
        return itemService.searchItem(requestText);

    }
}
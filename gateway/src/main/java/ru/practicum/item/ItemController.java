package ru.practicum.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") @Positive @NotNull Integer userId,
                                             @RequestBody @Valid CommentDto commentDto, @PathVariable @Positive @NotNull Integer itemId) {
        return itemClient.createComment(userId, itemId, commentDto);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody @NotNull @Valid ItemDto itemDto,
                                          @RequestHeader("X-Sharer-User-Id") @Positive Integer id) {
        return itemClient.createItem(id, itemDto);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody @NotNull @Valid ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") @NotNull @Positive Integer userId,
                                             @PathVariable("itemId") @NotNull @Positive Integer id) {
        return itemClient.updateItem(userId, id, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUsersItems(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Integer userId) {
        return itemClient.getUsersItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable("itemId") @Positive @NotNull Integer id,
                                          @RequestHeader("X-Sharer-User-Id") @Positive @NotNull Integer userId) {
        return itemClient.getItem(userId, id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam("text") String requestText) {
        return itemClient.search(requestText);

    }
}
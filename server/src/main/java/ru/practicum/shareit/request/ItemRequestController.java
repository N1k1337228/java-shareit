package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid ItemRequestDto requestDto) {
        return itemRequestService.createRequest(userId,requestDto);
    }

    @GetMapping
    public List<ResponseItemRequestDto> getAllUsersResponses(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getAllUsersResponses(userId);

    }

    @GetMapping("/all")
    public List<ResponseItemRequestDto> getAllResponses(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getAllResponses(userId);
    }

    @GetMapping("{requestId}")
    public ResponseItemRequestDto getRequestOnId(@PathVariable Integer requestId ,@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getRequestOnId(requestId,userId);
    }
}
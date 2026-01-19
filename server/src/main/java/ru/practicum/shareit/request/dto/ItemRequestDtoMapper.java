package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemResponse;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestDtoMapper {

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestDescription(itemRequestDto.getDescription());
        itemRequest.setTimeOfCreate(LocalDateTime.now());
        itemRequest.setRequester(user);
        return itemRequest;
    }

    public static ResponseItemRequestDto toResponseItemRequestDto(ItemRequest itemRequest) {
        ResponseItemRequestDto responseItemRequestDto = new ResponseItemRequestDto();
        responseItemRequestDto.setId(itemRequest.getId());
        responseItemRequestDto.setDescription(itemRequest.getRequestDescription());
        responseItemRequestDto.setCreated(itemRequest.getTimeOfCreate());

        if (itemRequest.getItemResponseList() == null || itemRequest.getItemResponseList().isEmpty()) {
            responseItemRequestDto.setItems(Collections.emptyList());
        } else {
            responseItemRequestDto.setItems(toItemResponseDtoList(itemRequest.getItemResponseList()));
        }
        return responseItemRequestDto;
    }

    private static ItemResponseDto toItemResponseDto(ItemResponse itemResponse) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(itemResponse.getId());
        itemResponseDto.setItemId(itemResponse.getItem() != null ? itemResponse.getItem().getId() : null);
        itemResponseDto.setUserId(itemResponse.getUser() != null ? itemResponse.getUser().getId() : null);
        if (itemResponse.getItemName() != null && !itemResponse.getItemName().isEmpty()) {
            itemResponseDto.setName(itemResponse.getItemName());
        }
        else if (itemResponse.getItem() != null && itemResponse.getItem().getName() != null) {
            itemResponseDto.setName(itemResponse.getItem().getName());
        }
        else {
            itemResponseDto.setName(null);
        }

        return itemResponseDto;
    }

    private static List<ItemResponseDto> toItemResponseDtoList(List<ItemResponse> itemResponses) {
        return itemResponses.stream()
                .map(ItemRequestDtoMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

    public static List<ResponseItemRequestDto> responseItemRequestDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequestDtoMapper::toResponseItemRequestDto)
                .collect(Collectors.toList());
    }
}
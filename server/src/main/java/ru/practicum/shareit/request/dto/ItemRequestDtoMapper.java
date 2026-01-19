package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemResponse;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
        if (itemRequest.getItemResponseList() == null) {
            responseItemRequestDto.setItems(Collections.emptyList());
        } else {
            responseItemRequestDto.setItems(toItemResponseDtoList(itemRequest.getItemResponseList()));
        }
        return responseItemRequestDto;
    }

    private static ItemResponseDto toItemResponseDto(ItemResponse itemResponse) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(itemResponse.getId());
        itemResponseDto.setItemId(itemResponse.getItem().getId());
        itemResponseDto.setUserId(itemResponse.getUser().getId());
        itemResponseDto.setName(itemResponse.getItemName());
        return itemResponseDto;
    }

    private static List<ItemResponseDto> toItemResponseDtoList(List<ItemResponse> itemResponses) {
        return itemResponses.stream()
                .map(ItemRequestDtoMapper::toItemResponseDto)
                .toList();
    }

    public static List<ResponseItemRequestDto> responseItemRequestDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequestDtoMapper::toResponseItemRequestDto)
                .toList();
    }


}

package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ResponseItemRequestDto createRequest(Integer userId,ItemRequestDto itemRequestDto);

    List<ResponseItemRequestDto> getAllUsersResponses(int userId);

    List<ResponseItemRequestDto> getAllResponses(int userId);

    ResponseItemRequestDto getRequestOnId(int requestId, int userId);

}

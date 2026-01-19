package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;

    public ResponseItemRequestDto createRequest(Integer userId,ItemRequestDto itemRequestDto) {
        User requester = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        ItemRequest itemRequest = ItemRequestDtoMapper.toItemRequest(itemRequestDto, requester);
        itemRequest = repository.save(itemRequest);
        return ItemRequestDtoMapper.toResponseItemRequestDto(itemRequest);
    }

    public List<ResponseItemRequestDto> getAllUsersResponses(int userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        List<ResponseItemRequestDto> responseItemRequestDtoList =
                ItemRequestDtoMapper.responseItemRequestDtoList(repository.findByRequesterId(userId));
        return responseItemRequestDtoList.stream()
                .sorted((r1, r2) -> r1.getCreated().compareTo(r2.getCreated()))
                .toList();
    }

    public List<ResponseItemRequestDto> getAllResponses(int userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        List<ResponseItemRequestDto> responseItemRequestDtoList = ItemRequestDtoMapper.responseItemRequestDtoList(repository.findAll());
        return responseItemRequestDtoList.stream()
                .sorted((r1, r2) -> r1.getCreated().compareTo(r2.getCreated()))
                .toList();
    }

    public ResponseItemRequestDto getRequestOnId(int requestId, int userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        ItemRequest itemRequest = repository.findById(requestId).orElseThrow(() -> new NotFoundException(""));
        return ItemRequestDtoMapper.toResponseItemRequestDto(itemRequest);
    }

}

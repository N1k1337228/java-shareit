package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@mail.com");
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Нужен дрель");
        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setRequestDescription("Нужен дрель");
        itemRequest.setRequester(user);
        itemRequest.setTimeOfCreate(LocalDateTime.now());
        itemRequest.setItemResponseList(Collections.emptyList());
    }

    @Test
    void createRequestTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        ResponseItemRequestDto result = itemRequestService.createRequest(user.getId(), itemRequestDto);
        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getRequestDescription(), result.getDescription());
    }

    @Test
    void createRequestNotFoundTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemRequestService.createRequest(user.getId(), itemRequestDto));

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getAllUsersResponsesTest() {
        List<ItemRequest> userRequests = List.of(itemRequest);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequesterId(anyInt())).thenReturn(userRequests);
        List<ResponseItemRequestDto> result = itemRequestService.getAllUsersResponses(user.getId());
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        ResponseItemRequestDto dto = result.get(0);
        assertEquals(itemRequest.getId(), dto.getId());
        assertEquals(itemRequest.getRequestDescription(), dto.getDescription());
    }

    @Test
    void getAllUsersResponsesNotFoundTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllUsersResponses(user.getId()));
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, never()).findByRequesterId(anyInt());
    }

    @Test
    void getAllResponsesTest() {
        List<ItemRequest> allRequests = List.of(itemRequest);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAll()).thenReturn(allRequests);
        List<ResponseItemRequestDto> result = itemRequestService.getAllResponses(user.getId());
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllResponsesNotFoundTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllResponses(user.getId()));
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, never()).findAll();
    }

    @Test
    void getRequestOnIdTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(itemRequest));
        ResponseItemRequestDto result = itemRequestService.getRequestOnId(itemRequest.getId(), user.getId());
        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getRequestDescription(), result.getDescription());
    }

    @Test
    void getRequestOnIdNotFoundTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestOnId(itemRequest.getId(), user.getId()));
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).findById(itemRequest.getId());
    }
}
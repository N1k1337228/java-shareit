package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.itemservise.ItemServiceImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dal.ItemResponseRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemResponse;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemResponseRepository itemResponseRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User testUser;
    private User anotherUser;
    private Item testItem;
    private ItemDto testItemDto;
    private ItemWithBookingsDto testItemWithBookingsDto;
    private ItemRequest testItemRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setName("Test User");
        testUser.setEmail("test@email.com");
        anotherUser = new User();
        anotherUser.setId(2);
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@email.com");
        testItem = new Item();
        testItem.setId(1);
        testItem.setName("Test Item");
        testItem.setDescription("Test Description");
        testItem.setAvailable(true);
        testItem.setOwner(testUser);
        testItemDto = new ItemDto();
        testItemDto.setId(1);
        testItemDto.setName("Test Item");
        testItemDto.setDescription("Test Description");
        testItemDto.setAvailable(true);
        testItemWithBookingsDto = new ItemWithBookingsDto();
        testItemWithBookingsDto.setId(1);
        testItemWithBookingsDto.setName("Test Item");
        testItemWithBookingsDto.setDescription("Test Description");
        testItemWithBookingsDto.setAvailable(true);
        testItemWithBookingsDto.setOwner(testUser.getId());  // ← теперь owner это Integer
        testItemWithBookingsDto.setComments(new ArrayList<>());
        testItemRequest = new ItemRequest();
        testItemRequest.setId(1);
        testItemRequest.setRequestDescription("Need item");
        testItemRequest.setRequester(anotherUser);
    }

    @Test
    void getItemTest() {
        int itemId = 1;
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));
        when(itemMapper.createItemDto(testItem)).thenReturn(testItemWithBookingsDto);
        when(bookingRepository.findLastBookingsForItems(anyList(), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findNextBookingsForItems(anyList(), any()))
                .thenReturn(Collections.emptyList());
        ItemWithBookingsDto result = itemService.getItem(itemId, userId);
        assertNotNull(result);
        assertEquals(testItem.getId(), result.getId());
        assertEquals(testUser.getId(), result.getOwner());
    }

    @Test
    void getItemUserIsNotOwnerTest() {
        int itemId = 1;
        int userId = 2;
        when(userRepository.findById(userId)).thenReturn(Optional.of(anotherUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));
        when(itemMapper.createItemDto(testItem)).thenReturn(testItemWithBookingsDto);
        ItemWithBookingsDto result = itemService.getItem(itemId, userId);
        assertNotNull(result);
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        verify(bookingRepository, never()).findLastBookingsForItems(anyList(), any());
        verify(bookingRepository, never()).findNextBookingsForItems(anyList(), any());
    }

    @Test
    void addItemWithItemRequestTest() {
        int userId = 1;
        ItemDto inputDto = new ItemDto();
        inputDto.setName("New Item");
        inputDto.setDescription("New Description");
        inputDto.setAvailable(true);
        inputDto.setRequestId(1);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(itemRequestRepository.findById(1)).thenReturn(Optional.of(testItemRequest));
        when(itemMapper.fromItemDto(inputDto)).thenReturn(testItem);
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);
        when(itemResponseRepository.save(any(ItemResponse.class))).thenReturn(new ItemResponse());
        when(itemMapper.toItemDto(testItem)).thenReturn(testItemDto);
        ItemDto result = itemService.addItem(inputDto, userId);
        assertNotNull(result);
        verify(itemRequestRepository).findById(1);
        verify(itemResponseRepository).save(any(ItemResponse.class));
        verify(itemRepository).save(testItem);
    }

    @Test
    void addItemWithoutItemRequestTest() {
        int userId = 1;
        ItemDto inputDto = new ItemDto();
        inputDto.setName("New Item");
        inputDto.setDescription("New Description");
        inputDto.setAvailable(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(itemMapper.fromItemDto(inputDto)).thenReturn(testItem);
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);
        when(itemMapper.toItemDto(testItem)).thenReturn(testItemDto);
        ItemDto result = itemService.addItem(inputDto, userId);
        assertNotNull(result);
        verify(itemRequestRepository, never()).findById(anyInt());
        verify(itemResponseRepository, never()).save(any());
        verify(itemRepository).save(testItem);
    }

    @Test
    void updateItemTest() {
        int itemId = 1;
        int userId = 1;
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Name");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);
        when(itemMapper.toItemDto(testItem)).thenReturn(testItemDto);
        ItemDto result = itemService.updateItem(updateDto, itemId, userId);
        assertNotNull(result);
        assertEquals("Updated Name", testItem.getName());
        assertEquals("Test Description", testItem.getDescription());
        assertTrue(testItem.getAvailable());
    }

    @Test
    void updateItemItemNotFoundTest() {
        int itemId = 999;
        int userId = 1;
        ItemDto updateDto = new ItemDto();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(updateDto, itemId, userId));
        assertTrue(exception.getMessage().contains("не найдена"));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getItemsListOnUsersIdTest() {
        int ownerId = 1;
        Item item2 = new Item();
        item2.setId(2);
        item2.setName("Item 2");
        item2.setOwner(testUser);
        List<Item> items = Arrays.asList(testItem, item2);
        when(itemRepository.findByOwnerId(ownerId)).thenReturn(items);
        when(itemMapper.createItemDto(testItem)).thenReturn(testItemWithBookingsDto);
        ItemWithBookingsDto dto2 = new ItemWithBookingsDto();
        dto2.setId(2);
        dto2.setOwner(ownerId);
        when(itemMapper.createItemDto(item2)).thenReturn(dto2);
        when(bookingRepository.findLastBookingsForItems(anyList(), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findNextBookingsForItems(anyList(), any()))
                .thenReturn(Collections.emptyList());
        List<ItemWithBookingsDto> result = itemService.getItemsListOnUsersId(ownerId);
        assertEquals(2, result.size());
        assertEquals(ownerId, result.get(0).getOwner());
        assertEquals(ownerId, result.get(1).getOwner());
    }

    @Test
    void searchItemAvailableTest() {
        String searchText = "test";
        testItem.setAvailable(true);
        List<Item> items = List.of(testItem);
        when(itemRepository.searchItem(searchText.toLowerCase())).thenReturn(items);
        when(itemMapper.toItemDto(testItem)).thenReturn(testItemDto);
        List<ItemDto> result = itemService.searchItem(searchText);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getAvailable());
    }

    @Test
    void searchItemNotAvailableTest() {
        String searchText = "test";
        testItem.setAvailable(false);
        List<Item> items = Collections.emptyList();
        when(itemRepository.searchItem(searchText.toLowerCase())).thenReturn(items);
        List<ItemDto> result = itemService.searchItem(searchText);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(itemRepository).searchItem(searchText.toLowerCase());
        verify(itemMapper, never()).toItemDto(any());
    }

    @Test
    void addCommentTest() {
        int itemId = 1;
        int userId = 2;
        CommentDto inputDto = new CommentDto();
        inputDto.setText("Great item!");
        Booking completedBooking = new Booking();
        completedBooking.setId(1);
        completedBooking.setItem(testItem);
        completedBooking.setBooker(anotherUser);
        completedBooking.setState(BookingStatus.APPROVED);
        completedBooking.setStartBooking(LocalDateTime.now().minusDays(5));
        completedBooking.setEndBooking(LocalDateTime.now().minusDays(2));
        Comment savedComment = new Comment();
        savedComment.setId(1);
        savedComment.setText("Great item!");
        savedComment.setCreated(LocalDateTime.now());
        savedComment.setOwnerOfComment(anotherUser);
        savedComment.setItem(testItem);
        when(userRepository.findById(userId)).thenReturn(Optional.of(anotherUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));
        when(bookingRepository.existsByBookerIdAndItemId(userId, itemId)).thenReturn(true);
        when(bookingRepository.findAll()).thenReturn(List.of(completedBooking));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        CommentDto result = itemService.addComment(itemId, userId, inputDto);
        assertNotNull(result);
        assertEquals("Great item!", result.getText());
    }

    @Test
    void addCommentBookingNotCompletedTest() {
        int itemId = 1;
        int userId = 2;
        CommentDto inputDto = new CommentDto();
        Booking futureBooking = new Booking();
        futureBooking.setId(1);
        futureBooking.setItem(testItem);
        futureBooking.setBooker(anotherUser);
        futureBooking.setState(BookingStatus.APPROVED);
        futureBooking.setStartBooking(LocalDateTime.now().plusDays(1));  // в будущем!
        futureBooking.setEndBooking(LocalDateTime.now().plusDays(3));
        when(userRepository.findById(userId)).thenReturn(Optional.of(anotherUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));
        when(bookingRepository.existsByBookerIdAndItemId(userId, itemId)).thenReturn(true);
        when(bookingRepository.findAll()).thenReturn(List.of(futureBooking));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.addComment(itemId, userId, inputDto));
        assertTrue(exception.getMessage().contains("Можно комментировать только после завершения"));
        verify(commentRepository, never()).save(any());
    }
}
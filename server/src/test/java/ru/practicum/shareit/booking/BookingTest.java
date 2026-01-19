package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BookingService bookingService;

    private User testUser;
    private User testOwner;
    private Item testItem;
    private Booking testBooking;
    private BookingDto testBookingDto;
    private ResponseBookingDto testResponseBookingDto;
    private ItemDto testItemDto;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setName("User");
        testUser.setEmail("user@email.com");
        testOwner = new User();
        testOwner.setId(2);
        testOwner.setName("Owner");
        testOwner.setEmail("owner@email.com");
        testItem = new Item();
        testItem.setId(1);
        testItem.setName("Дрель");
        testItem.setAvailable(true);
        testItem.setOwner(testOwner);
        testBooking = new Booking();
        testBooking.setId(1);
        testBooking.setItem(testItem);
        testBooking.setBooker(testUser);
        testBooking.setState(BookingStatus.WAITING);
        testBooking.setStartBooking(LocalDateTime.now().plusDays(1));
        testBooking.setEndBooking(LocalDateTime.now().plusDays(3));
        testBookingDto = new BookingDto();
        testBookingDto.setItemId(1);
        testBookingDto.setStart(LocalDateTime.now().plusDays(1));
        testBookingDto.setEnd(LocalDateTime.now().plusDays(3));
        testItemDto = new ItemDto();
        testItemDto.setId(1);
        testItemDto.setName("Дрель");
        testUserDto = new UserDto();
        testUserDto.setId(1);
        testUserDto.setName("User");
        testResponseBookingDto = new ResponseBookingDto();
        testResponseBookingDto.setId(1);
        testResponseBookingDto.setItem(testItemDto);
        testResponseBookingDto.setBooker(testUserDto);
    }

    @Test
    void addBookingTest() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(1)).thenReturn(Optional.of(testItem));
        when(bookingMapper.toBooking(any(), any(), any())).thenReturn(testBooking);
        when(bookingRepository.save(any())).thenReturn(testBooking);
        when(itemMapper.toItemDto(testItem)).thenReturn(testItemDto);
        when(userMapper.toUserDto(testUser)).thenReturn(testUserDto);
        when(bookingMapper.toResponseBookingDto(any(), any(), any()))
                .thenReturn(testResponseBookingDto);
        ResponseBookingDto result = bookingService.addBooking(testBookingDto, 1);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void addBookingOwnerEqualsBookerTest() {
        testItem.setOwner(testUser);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(1)).thenReturn(Optional.of(testItem));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(testBookingDto, 1));
        assertTrue(exception.getMessage().contains("Владелец не может бронировать свои вещи"));
    }

    @Test
    void updateBookingTest() {
        int bookingId = 1;
        int ownerId = 2;
        boolean approved = true;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        when(bookingRepository.save(any())).thenReturn(testBooking);
        when(itemMapper.toItemDto(testItem)).thenReturn(testItemDto);
        when(userMapper.toUserDto(testUser)).thenReturn(testUserDto);
        when(bookingMapper.toResponseBookingDto(any(), any(), any()))
                .thenReturn(testResponseBookingDto);
        ResponseBookingDto result = bookingService.confirmationOfRequest(bookingId, approved, ownerId);
        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, testBooking.getState());
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void updateBookingONotOwnerTest() {
        int bookingId = 1;
        int notOwnerId = 999;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.confirmationOfRequest(bookingId, true, notOwnerId));
        assertTrue(exception.getMessage().contains("Одобрять заявку может только владелец вещи"));
    }

    @Test
    void getBookingOnIdTest() {
        int userId = 1;
        int bookingId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(itemMapper.toItemDto(testItem)).thenReturn(testItemDto);
        when(userMapper.toUserDto(testUser)).thenReturn(testUserDto);
        when(bookingMapper.toResponseBookingDto(any(), any(), any()))
                .thenReturn(testResponseBookingDto);
        ResponseBookingDto result = bookingService.getBookingOnId(userId, bookingId);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void getBookingOnIdUserNotBookerTest() {
        int userId = 999;
        int bookingId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.getBookingOnId(userId, bookingId));
        assertTrue(exception.getMessage().contains("Запрашивать данные о бронировании может только"));
    }

    @Test
    void getBookingOnAllStateTest() {
        int bookerId = 1;
        BookingSort state = BookingSort.ALL;
        List<Booking> bookings = Collections.singletonList(testBooking);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(testUser));
        when(bookingRepository.findByBookerId(bookerId)).thenReturn(bookings);
        when(bookingMapper.toResponseBookingDtoList(bookings))
                .thenReturn(Collections.singletonList(testResponseBookingDto));
        List<ResponseBookingDto> result = bookingService.getBookingOnState(bookerId, state);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getBookingOnCurrentStateTest() {
        int bookerId = 1;
        BookingSort state = BookingSort.CURRENT;
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(testUser));
        when(bookingRepository.getAllCurrentBookingOnBookerId(eq(bookerId), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(testBooking));
        when(bookingMapper.toResponseBookingDtoList(anyList()))
                .thenReturn(Collections.singletonList(testResponseBookingDto));
        List<ResponseBookingDto> result = bookingService.getBookingOnState(bookerId, state);
        assertNotNull(result);
        verify(bookingRepository).getAllCurrentBookingOnBookerId(eq(bookerId), any(LocalDate.class));
    }

    @Test
    void getBookingOnStateAllStateTest() {
        int ownerId = 2;
        BookingSort state = BookingSort.ALL;
        List<Booking> bookings = Collections.singletonList(testBooking);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(testOwner));
        when(bookingRepository.getAllBookingOnOwnerId(ownerId)).thenReturn(bookings);
        when(bookingMapper.toResponseBookingDtoList(bookings))
                .thenReturn(Collections.singletonList(testResponseBookingDto));
        List<ResponseBookingDto> result = bookingService.getBookingOnStateAndOwnerId(ownerId, state);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getBookingOnWaitingStateTest() {
        int ownerId = 2;
        BookingSort state = BookingSort.WAITING;
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(testOwner));
        when(bookingRepository.getAllBookingOnOwnerIdAndState(ownerId, "WAITING"))
                .thenReturn(Collections.singletonList(testBooking));
        when(bookingMapper.toResponseBookingDtoList(anyList()))
                .thenReturn(Collections.singletonList(testResponseBookingDto));
        List<ResponseBookingDto> result = bookingService.getBookingOnStateAndOwnerId(ownerId, state);
        assertNotNull(result);
        verify(bookingRepository).getAllBookingOnOwnerIdAndState(ownerId, "WAITING");
    }
}
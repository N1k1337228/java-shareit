package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=always"
},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class BookingRepositoryTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item availableItem;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        owner = userRepository.save(owner);
        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@mail.com");
        booker = userRepository.save(booker);
        availableItem = new Item();
        availableItem.setName("Available Item");
        availableItem.setDescription("Description");
        availableItem.setAvailable(true);
        availableItem.setOwner(owner);
        availableItem = itemRepository.save(availableItem);
    }

    @Test
    void addBookingTest() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(availableItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        ResponseBookingDto result = bookingService.addBooking(bookingDto, booker.getId());
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertEquals(availableItem.getId(), result.getItem().getId());
        assertEquals(booker.getId(), result.getBooker().getId());
    }

    @Test
    void confirmationOfRequestTest() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(availableItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        ResponseBookingDto createdBooking = bookingService.addBooking(bookingDto, booker.getId());
        ResponseBookingDto result = bookingService.confirmationOfRequest(
                createdBooking.getId(), true, owner.getId());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
        assertEquals(createdBooking.getId(), result.getId());
    }

    @Test
    void getBookingOnIdTest() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(availableItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        ResponseBookingDto createdBooking = bookingService.addBooking(bookingDto, booker.getId());
        ResponseBookingDto result = bookingService.getBookingOnId(
                booker.getId(), createdBooking.getId());
        assertNotNull(result);
        assertEquals(createdBooking.getId(), result.getId());
        assertEquals(booker.getId(), result.getBooker().getId());
    }

    @Test
    void getBookingOnStateTest() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(availableItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.addBooking(bookingDto, booker.getId());
        List<ResponseBookingDto> result = bookingService.getBookingOnState(
                booker.getId(), BookingSort.ALL);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
    }

    @Test
    void getBookingOnStateAndOwnerIdTest() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(availableItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.addBooking(bookingDto, booker.getId());
        List<ResponseBookingDto> result = bookingService.getBookingOnStateAndOwnerId(
                owner.getId(), BookingSort.ALL);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        ResponseBookingDto booking = result.get(0);
        assertNotNull(booking);
        assertNotNull(booking.getItem());
        assertEquals(availableItem.getId(), booking.getItem().getId());
        assertEquals(booker.getId(), booking.getBooker().getId());
    }
}
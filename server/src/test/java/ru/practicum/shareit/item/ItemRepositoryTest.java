package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.itemservise.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItApp.class, properties = {
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=always"
},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class ItemRepositoryTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User owner;
    private User booker;
    private Item item;

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
        item = new Item();
        item.setName("Item 1");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setComment(Collections.emptyList());
        item = itemRepository.save(item);
    }

    @Test
    void getItemTest() {
        ItemWithBookingsDto result = itemService.getItem(item.getId(), owner.getId());
        assertNotNull(result);
    }

    @Test
    void addItem_IntegrationTest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("New Item");
        itemDto.setDescription("New Description");
        itemDto.setAvailable(true);
        ItemDto result = itemService.addItem(itemDto, owner.getId());
        assertNotNull(result);
        assertEquals("New Item", result.getName());
        assertEquals("New Description", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void updateItemTest() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated Description");
        updateDto.setAvailable(false);
        ItemDto result = itemService.updateItem(updateDto, item.getId(), owner.getId());
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void getItemsListOnUsersIdTest() {
        List<ItemWithBookingsDto> result = itemService.getItemsListOnUsersId(owner.getId());
        assertNotNull(result);
    }

    @Test
    void searchItemTest() {
        List<ItemDto> resultByName = itemService.searchItem("Item");
        assertNotNull(resultByName);
        assertFalse(resultByName.isEmpty());
        List<ItemDto> resultEmpty = itemService.searchItem("");
        assertNotNull(resultEmpty);
        assertTrue(resultEmpty.isEmpty());
    }

    @Test
    void addCommentTest() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStartBooking(LocalDateTime.now().minusDays(2));
        booking.setEndBooking(LocalDateTime.now().minusDays(1));
        booking.setState(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");
        CommentDto result = itemService.addComment(item.getId(), booker.getId(), commentDto);
        assertNotNull(result);
        assertEquals("Great item!", result.getText());
        assertNotNull(result.getCreated());
    }
}
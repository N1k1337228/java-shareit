package ru.practicum.shareit.request;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
class RequestRepositoryTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user1;
    private User user2;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@mail.com");
        user1 = userRepository.save(user1);
        user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@mail.com");
        user2 = userRepository.save(user2);
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Нужен перфоратор");
    }

    @Test
    void createRequestTest() {
        ResponseItemRequestDto result = itemRequestService.createRequest(user1.getId(), itemRequestDto);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Нужен перфоратор", result.getDescription());
        assertNotNull(result.getCreated());
        assertNotNull(result.getItems());
        assertEquals(1, itemRequestRepository.count());
    }

    @Test
    void getAllUsersResponsesTest() {
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Запрос 1");
        itemRequestService.createRequest(user1.getId(), requestDto1);
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Запрос 2");
        itemRequestService.createRequest(user1.getId(), requestDto2);
        ItemRequestDto requestDto3 = new ItemRequestDto();
        requestDto3.setDescription("Запрос от user2");
        itemRequestService.createRequest(user2.getId(), requestDto3);
        List<ResponseItemRequestDto> result = itemRequestService.getAllUsersResponses(user1.getId());
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllResponsesTest() {
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Запрос 1");
        itemRequestService.createRequest(user1.getId(), requestDto1);
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Запрос 2");
        itemRequestService.createRequest(user2.getId(), requestDto2);
        List<ResponseItemRequestDto> result = itemRequestService.getAllResponses(user1.getId());
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getRequestOnIdTest() {
        ResponseItemRequestDto createdRequest = itemRequestService.createRequest(user1.getId(), itemRequestDto);
        int requestId = createdRequest.getId();
        ResponseItemRequestDto result = itemRequestService.getRequestOnId(requestId, user1.getId());
        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals("Нужен перфоратор", result.getDescription());
    }
}
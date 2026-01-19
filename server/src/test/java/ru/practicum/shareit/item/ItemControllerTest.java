package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.itemservise.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemController.class)
@ContextConfiguration(classes = ShareItApp.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;
    private ItemDto itemDto;
    private ItemWithBookingsDto itemWithBookingsDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Дрель");
        itemDto.setDescription("Аккумуляторная дрель");
        itemDto.setAvailable(true);
        itemWithBookingsDto = new ItemWithBookingsDto();
        itemWithBookingsDto.setId(1);
        itemWithBookingsDto.setName("Дрель");
        itemWithBookingsDto.setDescription("Аккумуляторная дрель");
        itemWithBookingsDto.setAvailable(true);
        itemWithBookingsDto.setOwner(1);
        commentDto = new CommentDto();
        commentDto.setId(1);
        commentDto.setText("Отличная дрель!");
        commentDto.setAuthorName("Иван");
        commentDto.setCreated(LocalDateTime.now());
    }

    @Test
    void createItemTest() throws Exception {
        when(itemService.addItem(any(ItemDto.class), anyInt()))
                .thenReturn(itemDto);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void createItemNoUserIdTest() throws Exception {
        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItemTest() throws Exception {
        itemDto.setName("Новая дрель");
        when(itemService.updateItem(any(ItemDto.class), anyInt(), anyInt()))
                .thenReturn(itemDto);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Новая дрель"));
    }

    @Test
    void getUsersItemsTest() throws Exception {
        List<ItemWithBookingsDto> items = Collections.singletonList(itemWithBookingsDto);
        when(itemService.getItemsListOnUsersId(anyInt()))
                .thenReturn(items);
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getUsersItemsNoItemsTest() throws Exception {
        when(itemService.getItemsListOnUsersId(anyInt()))
                .thenReturn(List.of());
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getItemTest() throws Exception {
        when(itemService.getItem(anyInt(), anyInt()))
                .thenReturn(itemWithBookingsDto);
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void getItemNoUserIdTest() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchItemsTest() throws Exception {
        List<ItemDto> items = Collections.singletonList(itemDto);
        when(itemService.searchItem(anyString()))
                .thenReturn(items);
        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    @Test
    void searchItemsEmptyTextTest() throws Exception {
        when(itemService.searchItem(anyString()))
                .thenReturn(List.of());
        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void addCommentTest() throws Exception {
        when(itemService.addComment(anyInt(), anyInt(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Отличная дрель!"));
    }
}
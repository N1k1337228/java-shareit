package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class RequestControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createRequestTest() throws Exception {
        Integer userId = 1;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Нужен дрель");
        ResponseItemRequestDto responseDto = new ResponseItemRequestDto();
        responseDto.setId(1);
        responseDto.setDescription("Нужен дрель");
        responseDto.setCreated(LocalDateTime.now());
        when(itemRequestService.createRequest(anyInt(), any(ItemRequestDto.class)))
                .thenReturn(responseDto);
        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужен дрель"))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    void createRequestBlankDescriptionTest() throws Exception {
        Integer userId = 1;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("");
        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllUsersResponsesTest() throws Exception {
        Integer userId = 1;
        ResponseItemRequestDto responseDto1 = new ResponseItemRequestDto();
        responseDto1.setId(1);
        responseDto1.setDescription("Нужен дрель");
        responseDto1.setCreated(LocalDateTime.now().minusDays(1));
        ResponseItemRequestDto responseDto2 = new ResponseItemRequestDto();
        responseDto2.setId(2);
        responseDto2.setDescription("Нужен перфоратор");
        responseDto2.setCreated(LocalDateTime.now());
        List<ResponseItemRequestDto> responseList = List.of(responseDto1, responseDto2);
        when(itemRequestService.getAllUsersResponses(anyInt()))
                .thenReturn(responseList);
        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужен дрель"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("Нужен перфоратор"));
    }

    @Test
    void getAllUsersResponsesInternalServerErrorTest() throws Exception {
        Integer userId = 1;
        when(itemRequestService.getAllUsersResponses(anyInt()))
                .thenThrow(new RuntimeException("Service error"));
        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllResponsesTest() throws Exception {
        Integer userId = 1;
        ResponseItemRequestDto responseDto = new ResponseItemRequestDto();
        responseDto.setId(1);
        responseDto.setDescription("Общий запрос");
        responseDto.setCreated(LocalDateTime.now());
        List<ResponseItemRequestDto> responseList = List.of(responseDto);
        when(itemRequestService.getAllResponses(anyInt()))
                .thenReturn(responseList);
        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Общий запрос"));
    }

    @Test
    void getAllResponsesUserIdHeaderTest() throws Exception {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestOnIdTest() throws Exception {
        Integer userId = 1;
        Integer requestId = 123;
        ResponseItemRequestDto responseDto = new ResponseItemRequestDto();
        responseDto.setId(requestId);
        responseDto.setDescription("Конкретный запрос");
        responseDto.setCreated(LocalDateTime.now());
        when(itemRequestService.getRequestOnId(anyInt(), anyInt()))
                .thenReturn(responseDto);
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Конкретный запрос"));
    }

    @Test
    void getRequestOnIdNotFoundTest() throws Exception {
        Integer userId = 1;
        Integer requestId = 999;
        when(itemRequestService.getRequestOnId(anyInt(), anyInt()))
                .thenReturn(null); // Имитация не найденного запроса
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void createRequestUserIdHeaderTest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Нужен инструмент");
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }
}
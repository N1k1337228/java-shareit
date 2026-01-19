package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void addBookingTest() throws Exception {
        BookingDto inputDto = new BookingDto();
        inputDto.setItemId(1);
        inputDto.setStart(LocalDateTime.now().plusDays(1));
        inputDto.setEnd(LocalDateTime.now().plusDays(2));
        ResponseBookingDto responseDto = new ResponseBookingDto();
        responseDto.setId(1);
        responseDto.setStatus(BookingStatus.WAITING);
        when(bookingService.addBooking(any(BookingDto.class), eq(123)))
                .thenReturn(responseDto);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 123)
                        .content(objectMapper.writeValueAsString(inputDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void addBookingNotFoundUserTest() throws Exception {
        BookingDto inputDto = new BookingDto();
        inputDto.setItemId(1);
        when(bookingService.addBooking(any(BookingDto.class), eq(999)))
                .thenThrow(new NotFoundException("Пользователь не найден"));
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 999)
                        .content(objectMapper.writeValueAsString(inputDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBookingTest() throws Exception {
        ResponseBookingDto responseDto = new ResponseBookingDto();
        responseDto.setId(1);
        responseDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.confirmationOfRequest(1, true, 123))
                .thenReturn(responseDto);
        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 123)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void updateBookingNotFoundBookingTest() throws Exception {
        when(bookingService.confirmationOfRequest(999, true, 123))
                .thenThrow(new NotFoundException("Бронирование не найдено"));
        mockMvc.perform(patch("/bookings/{bookingId}", 999)
                        .header("X-Sharer-User-Id", 123)
                        .param("approved", "true"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingTest() throws Exception {
        ResponseBookingDto responseDto = new ResponseBookingDto();
        responseDto.setId(1);
        responseDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.getBookingOnId(123, 1))
                .thenReturn(responseDto);
        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 123))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingNoAccessToBooking() throws Exception {
        when(bookingService.getBookingOnId(123, 1))
                .thenThrow(new NotFoundException("Нет доступа"));
        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 123))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingOnStateTest() throws Exception {
        ResponseBookingDto booking1 = new ResponseBookingDto();
        booking1.setId(1);
        ResponseBookingDto booking2 = new ResponseBookingDto();
        booking2.setId(2);
        when(bookingService.getBookingOnState(eq(123), any(BookingSort.class)))
                .thenReturn(List.of(booking1, booking2));
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 123)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getBookingOnUnknownStateTest() throws Exception {
        when(bookingService.getBookingOnState(eq(123), any(BookingSort.class)))
                .thenThrow(new ValidationException("Unknown state: UNSUPPORTED_STATUS"));
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 123)
                        .param("state", "UNSUPPORTED_STATUS"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getBookingOfCurrentUserTest() throws Exception {
        ResponseBookingDto booking = new ResponseBookingDto();
        booking.setId(1);
        when(bookingService.getBookingOnStateAndOwnerId(eq(123), any(BookingSort.class)))
                .thenReturn(List.of(booking));
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 123)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // Негативный: Пользователь не найден
    @Test
    void getBookingOfCurrentUserNotFoundUserTest() throws Exception {
        when(bookingService.getBookingOnStateAndOwnerId(eq(999), any(BookingSort.class)))
                .thenThrow(new NotFoundException("Пользователь не найден"));
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 999)
                        .param("state", "ALL"))
                .andExpect(status().isNotFound());
    }
}
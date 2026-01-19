package ru.practicum.booking;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    private Integer itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}

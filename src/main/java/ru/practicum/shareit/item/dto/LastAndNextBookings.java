package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;

import java.util.Map;

@Data
@AllArgsConstructor
public class LastAndNextBookings {
    private Map<Integer, Booking> lastBookings;
    private Map<Integer, Booking> nextBookings;
}
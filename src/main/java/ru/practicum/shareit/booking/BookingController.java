package ru.practicum.shareit.booking;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseBookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto confirmationOfRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                    @PathVariable @NonNull Integer bookingId, @RequestParam("approved") Boolean approved) {
        return bookingService.confirmationOfRequest(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @PathVariable Integer bookingId) {
        return bookingService.getBookingOnId(userId, bookingId);
    }

    @GetMapping
    public List<ResponseBookingDto> getBookingOnState(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                      @RequestParam(value = "state",
                                                              required = false, defaultValue = "ALL") BookingSort state) {
        return bookingService.getBookingOnState(userId, state);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getBookingOfCurrentUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                            @RequestParam(value = "state", required = false,
                                                                    defaultValue = "ALL") BookingSort state) {
        return bookingService.getBookingOnStateAndOwnerId(userId, state);
    }
}
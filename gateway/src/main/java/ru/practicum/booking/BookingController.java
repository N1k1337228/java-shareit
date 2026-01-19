package ru.practicum.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") @NonNull @Positive Integer userId, @Valid @RequestBody BookingDto bookingDto) {
        log.info("Gateway received: {}", bookingDto);  // ← ЛОГ
        return bookingClient.bookItem(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmationOfRequest(@RequestHeader("X-Sharer-User-Id") @Positive Integer userId, @PathVariable @NonNull @Positive Integer bookingId, @RequestParam("approved") String approved) {
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") @Positive Integer userId, @PathVariable @Positive Integer bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingOnState(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        return bookingClient.getBookingOnState(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingOfCurrentUser(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        return bookingClient.getBookingOfCurrentUser(userId, state);
    }
}

package ru.practicum.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.booking.BookingDto;



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
        return bookingClient.bookItem(userId,bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmationOfRequest(@RequestHeader("X-Sharer-User-Id") @Positive Integer userId, @PathVariable @NonNull @Positive Integer bookingId, @RequestParam("approved") String approved) {
        return bookingClient.updateBooking(userId,bookingId,approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") @Positive Integer userId, @PathVariable @Positive Integer bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingOnState(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        return bookingClient.getBookingOnState(userId,state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingOfCurrentUser(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        return bookingClient.getBookingOfCurrentUser(userId,state);
    }
}

//	@GetMapping
//	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
//			@RequestParam(name = "state", defaultValue = "all") String stateParam,
//			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
//			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
//		BookingState state = BookingState.from(stateParam)
//				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
//		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
//		return bookingClient.getBookings(userId, state, from, size);
//	}
//
//	@PostMapping
//	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
//			@RequestBody @Valid BookItemRequestDto requestDto) {
//		log.info("Creating booking {}, userId={}", requestDto, userId);
//		return bookingClient.bookItem(userId, requestDto);
//	}
//
//	@GetMapping("/{bookingId}")
//	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
//			@PathVariable Long bookingId) {
//		log.info("Get booking {}, userId={}", bookingId, userId);
//		return bookingClient.getBooking(userId, bookingId);
//	}

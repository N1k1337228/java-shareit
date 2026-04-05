package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    public Booking toBooking(BookingDto bookingDto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStartBooking(bookingDto.getStart());
        booking.setEndBooking(bookingDto.getEnd());
        return booking;
    }

    public BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setStart(booking.getStartBooking());
        bookingDto.setEnd(booking.getEndBooking());
        return bookingDto;
    }

    public List<BookingDto> bookingListToBookingDtoList(List<Booking> bookingsList) {
        return bookingsList.stream()
                .map(this::toBookingDto)
                .toList();
    }

    public ResponseBookingDto toResponseBookingDto(Booking booking, ItemDto itemDto, UserDto userDto) {
        ResponseBookingDto responseBookingDto = new ResponseBookingDto();
        responseBookingDto.setId(booking.getId());
        responseBookingDto.setBooker(userDto);
        responseBookingDto.setItem(itemDto);
        responseBookingDto.setStart(booking.getStartBooking());
        responseBookingDto.setEnd(booking.getEndBooking());
        responseBookingDto.setStatus(booking.getState());
        return responseBookingDto;
    }

    public ResponseBookingDto toResponseBookingDto12(Booking booking) {
        ResponseBookingDto dto = new ResponseBookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStartBooking());
        dto.setEnd(booking.getEndBooking());
        dto.setStatus(booking.getState());
        UserDto userDto = new UserDto();
        userDto.setId(booking.getBooker().getId());
        userDto.setName(booking.getBooker().getName());
        userDto.setEmail(booking.getBooker().getEmail());
        dto.setBooker(userDto);
        ItemDto itemDto = new ItemDto();
        itemDto.setId(booking.getItem().getId());
        itemDto.setName(booking.getItem().getName());
        itemDto.setDescription(booking.getItem().getDescription());
        itemDto.setAvailable(booking.getItem().getAvailable());
        dto.setItem(itemDto);
        return dto;
    }

    public List<ResponseBookingDto> toResponseBookingDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::toResponseBookingDto12)
                .collect(Collectors.toList());
    }
}
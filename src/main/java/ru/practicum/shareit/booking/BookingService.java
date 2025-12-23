package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserReposirory;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserReposirory userReposirory;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public ResponseBookingDto addBooking(BookingDto bookingDto, int bookerId) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Дата окончания бронирования не может быть раньше даты начала");
        }
        User user = userReposirory.findById(bookerId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!item.getAvailable()) {
            log.error("");
            throw new ValidationException("Нельзя забронировать вещь, которая уже забронирована");
        }
        Booking booking = bookingMapper.toBooking(bookingDto, item, user);
        booking.setState(BookingStatus.WAITING.toString());
        booking = bookingRepository.save(booking);
        return bookingMapper.toResponseBookingDto(booking, itemMapper.toItemDto(item), userMapper.toUserDto(user));
    }

    public ResponseBookingDto confirmationOfRequest(int bookingId, String approved, int ownerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(""));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new NotFoundException(""));
        if (item.getOwner().getId().equals(ownerId)) {
            switch (approved) {
                case "true":
                    booking.setState(BookingStatus.APPROVED.toString());
                    break;
                case "false":
                    booking.setState(BookingStatus.REJECTED.toString());
                    break;
            }
            bookingRepository.save(booking);
            return bookingMapper.toResponseBookingDto(booking, itemMapper.toItemDto(item), userMapper.toUserDto(booking.getBooker()));
        }
        throw new ValidationException("Одобрять заявку может только владелец вещи");
    }

    public ResponseBookingDto getBookingOnId(Integer userId, Integer bookingId) {
        User user = userReposirory.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        Integer bookerId = booking.getBooker().getId();
        Integer ownerId = booking.getItem().getOwner().getId();
        if (!(userId.equals(bookerId) || userId.equals(ownerId))) {
            log.error("");
            throw new ValidationException("Запрашивать данные о бронировании может только " +
                    "создатель хозяин забронированной вещи или пользователь забронировавший её");
        }
        log.info("");
        return bookingMapper.toResponseBookingDto(booking, itemMapper.toItemDto(booking.getItem()), userMapper.toUserDto(user));
    }

    public List<ResponseBookingDto> getBookingOnState(int bookerId, String state) {
        User booker = userReposirory.findById(bookerId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        switch (state.toUpperCase()) {
            case "ALL":
                return bookingMapper.toResponseBookingDtoList(bookingRepository.findByBookerId(bookerId));
            case "CURRENT":
                return bookingMapper.toResponseBookingDtoList(bookingRepository
                        .getAllCurrentBookingOnBookerId(bookerId, LocalDate.now()));
            case "PAST":
                return bookingMapper.toResponseBookingDtoList(bookingRepository
                        .findAllByBookerIdAndEndBookingBefore(bookerId, LocalDate.now()));
            case "FUTURE":
                return bookingMapper.toResponseBookingDtoList(bookingRepository
                        .findAllByBookerIdAndStartBookingAfter(bookerId, LocalDate.now()));
            case "WAITING":
                return bookingMapper.toResponseBookingDtoList(bookingRepository
                        .findAllByBookerIdAndState(bookerId, "WAITING"));
            case "REJECTED":
                return bookingMapper.toResponseBookingDtoList(bookingRepository
                        .findAllByBookerIdAndState(bookerId, "REJECTED"));
            default:
                log.error("Переданный state не соответствует допустимым значениям: " +
                        "(ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)");
                throw new ValidationException("Переданный state не соответствует допустимым значениям: " +
                        "(ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)");
        }
    }

    public List<BookingDto> getBookingOnStateAndOwnerId(int ownerId, String state) {
        User owner = userReposirory.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (itemRepository.getCountOfOwnersItems(ownerId) < 1) {
            throw new ValidationException("У пользователя ещё не загрузил вещи");
        }
        switch (state) {
            case "ALL":
                return bookingMapper.bookingListToBookingDtoList(bookingRepository.getAllBookingOnOwnerId(ownerId));
            case "CURRENT":
                return bookingMapper.bookingListToBookingDtoList(bookingRepository
                        .getAllCurrentBookingOnOwnerId(ownerId, LocalDate.now()));
            case "PAST":
                return bookingMapper.bookingListToBookingDtoList(bookingRepository
                        .getAllPastBookingOnOwnerId(ownerId, LocalDate.now()));
            case "FUTURE":
                return bookingMapper.bookingListToBookingDtoList(bookingRepository
                        .getAllFutureBookingOnOwnerId(ownerId, LocalDate.now()));
            case "WAITING":
                return bookingMapper.bookingListToBookingDtoList(bookingRepository
                        .getAllBookingOnOwnerIdAndState(ownerId, "WAITING"));
            case "REJECTED":
                return bookingMapper.bookingListToBookingDtoList(bookingRepository
                        .getAllBookingOnOwnerIdAndState(ownerId, "REJECTED"));
            default:
                log.error("Переданный state не соответствует допустимым значениям: " +
                        "(ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)");
                throw new ValidationException("Переданный state не соответствует допустимым значениям: " +
                        "(ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)");
        }
    }
}
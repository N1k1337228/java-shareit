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
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userReposirory;
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
            log.error("Попытка забронировать вещь, которая уже забронирована");
            throw new ValidationException("Нельзя забронировать вещь, которая уже забронирована");
        }
        if (item.getOwner().getId() == bookerId) {
            log.error("Владелец попытался забронировать свою вещь");
            throw new ValidationException("Владелец не может бронировать свои вещи");
        }
        Booking booking = bookingMapper.toBooking(bookingDto, item, user);
        booking.setState(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);
        return bookingMapper.toResponseBookingDto(booking, itemMapper.toItemDto(item), userMapper.toUserDto(user));
    }

    public ResponseBookingDto confirmationOfRequest(int bookingId, String approved, int ownerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!booking.getState().toString().equals(BookingStatus.WAITING.toString())) {
            log.error("Попытка принять или отклонить бронирование, которые уже не находятся в статусе WAITING");
            throw new ValidationException("Принимать или отклонять бронирование можно " +
                    "только если запрос не была до этого принят или отклонён");
        }
        if (item.getOwner().getId().equals(ownerId)) {
            boolean isApproved = Boolean.parseBoolean(approved);
            if (isApproved) {
                booking.setState(BookingStatus.APPROVED);
            } else {
                booking.setState(BookingStatus.REJECTED);
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
            log.error("Попытка запросить данные о бронировании пользователем, не являющимся арендатором/владельцем");
            throw new ValidationException("Запрашивать данные о бронировании может только " +
                    "хозяин забронированной вещи или пользователь забронировавший её");
        }
        log.info("Запросили бронирование");
        return bookingMapper.toResponseBookingDto(booking, itemMapper.toItemDto(booking.getItem()), userMapper.toUserDto(user));
    }

    public List<ResponseBookingDto> getBookingOnState(int bookerId, BookingSort state) {
        User booker = userReposirory.findById(bookerId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        switch (state) {
            case BookingSort.ALL:
                return bookingMapper.toResponseBookingDtoList(bookingRepository.findByBookerId(bookerId));
            case BookingSort.CURRENT:
                return bookingMapper.toResponseBookingDtoList(bookingRepository
                        .getAllCurrentBookingOnBookerId(bookerId, LocalDate.now()));
            case BookingSort.PAST:
                return bookingMapper.toResponseBookingDtoList(bookingRepository
                        .findAllByBookerIdAndEndBookingBefore(bookerId, LocalDate.now()));
            case BookingSort.FUTURE:
                return bookingMapper.toResponseBookingDtoList(bookingRepository
                        .findAllByBookerIdAndStartBookingAfter(bookerId, LocalDate.now()));
            case BookingSort.WAITING:
                return bookingMapper.toResponseBookingDtoList(bookingRepository
                        .findAllByBookerIdAndState(bookerId, "WAITING"));
            case BookingSort.REJECTED:
                return bookingMapper.toResponseBookingDtoList(bookingRepository
                        .findAllByBookerIdAndState(bookerId, "REJECTED"));
            default:
                log.error("Переданный state не соответствует допустимым значениям: " +
                        "(ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)");
                throw new ValidationException("Переданный state не соответствует допустимым значениям: " +
                        "(ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)");
        }
    }

    public List<ResponseBookingDto> getBookingOnStateAndOwnerId(int ownerId, BookingSort state) {
        User owner = userReposirory.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        switch (state) {
            case BookingSort.ALL:
                return bookingMapper.toResponseBookingDtoList(bookingRepository.getAllBookingOnOwnerId(ownerId));
            case BookingSort.CURRENT:
                return bookingMapper.toResponseBookingDtoList(bookingRepository
                        .getAllCurrentBookingOnOwnerId(ownerId, LocalDate.now()));
            case BookingSort.PAST:
                return bookingMapper.toResponseBookingDtoList(bookingRepository
                        .getAllPastBookingOnOwnerId(ownerId, LocalDate.now()));
            case BookingSort.FUTURE:
                return bookingMapper.toResponseBookingDtoList(bookingRepository
                        .getAllFutureBookingOnOwnerId(ownerId, LocalDate.now()));
            case BookingSort.WAITING:
                return bookingMapper.toResponseBookingDtoList(bookingRepository
                        .getAllBookingOnOwnerIdAndState(ownerId, "WAITING"));
            case BookingSort.REJECTED:
                return bookingMapper.toResponseBookingDtoList(bookingRepository
                        .getAllBookingOnOwnerIdAndState(ownerId, "REJECTED"));
            default:
                log.error("Переданный state не соответствует допустимым значениям: " +
                        "(ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)");
                throw new ValidationException("Переданный state не соответствует допустимым значениям: " +
                        "(ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)");
        }
    }
}
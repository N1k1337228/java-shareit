package ru.practicum.shareit.item.itemservise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserReposirory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserReposirory userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;

    public ItemWithBookingsDto getItem(int itemId, int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
        boolean isOwner = item.getOwner().getId().equals(userId);
        if (!isOwner) {
            ItemWithBookingsDto dto = new ItemWithBookingsDto();
            dto.setId(item.getId());
            dto.setName(item.getName());
            dto.setDescription(item.getDescription());
            dto.setAvailable(item.getAvailable());
            dto.setOwner(item.getOwner().getId());
            dto.setComments(CommentMapper.toCommentDtoList(item.getComment()));
            dto.setLastBooking(null);
            dto.setNextBooking(null);
            return dto;
        }
        List<Integer> itemIds = List.of(itemId);
        LocalDateTime now = LocalDateTime.now();
        Map<Integer, Booking> lastBookingsMap = bookingRepository
                .findLastBookingsForItems(itemIds, now)
                .stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        booking -> booking,
                        (first, second) -> {
                            return first.getEndBooking().isAfter(second.getEndBooking())
                                    ? first : second;
                        }
                ));
        Map<Integer, Booking> nextBookingsMap = bookingRepository
                .findNextBookingsForItems(itemIds, now)
                .stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        booking -> booking,
                        (first, second) -> {
                            return first.getStartBooking().isBefore(second.getStartBooking())
                                    ? first : second;
                        }
                ));
        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwner(item.getOwner().getId());
        dto.setComments(CommentMapper.toCommentDtoList(item.getComment()));
        fillBookingDates(dto, nextBookingsMap, lastBookingsMap, item.getId(),
                userId, item.getOwner().getId());
        return dto;
    }

    public ItemDto addItem(ItemDto itemDto, int userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Незарегистрированный пользователь не может добавлять вещи"));
        Item item = itemMapper.fromItemDto(itemDto);
        item.setOwner(user);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto updateItem(ItemDto itemDto, int itemId, int userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Незарегистрированный пользователь не может обновлять вещи"));
        Item oldItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с id "
                + itemId + "не найдена"));
        if (!oldItem.getOwner().getId().equals(userId)) {
            log.error("Попытка обновить вещь с id {} не её владельцем", itemId);
            throw new ValidationException("Обновлять вещь может только её владелец");
        }
        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toItemDto(itemRepository.save(oldItem));
    }

    public List<ItemWithBookingsDto> getItemsListOnUsersId(int ownerId) {
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();
        Map<Integer, Booking> lastBookingsMap = bookingRepository
                .findLastBookingsForItems(itemIds, now)
                .stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        booking -> booking,
                        (first, second) -> {
                            return first.getEndBooking().isAfter(second.getEndBooking())
                                    ? first : second;
                        }
                ));
        Map<Integer, Booking> nextBookingsMap = bookingRepository
                .findNextBookingsForItems(itemIds, now)
                .stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        booking -> booking,
                        (first, second) -> {
                            return first.getStartBooking().isBefore(second.getStartBooking())
                                    ? first : second;
                        }
                ));
        List<ItemWithBookingsDto> result = new ArrayList<>();
        for (Item item : items) {
            ItemWithBookingsDto dto = new ItemWithBookingsDto();
            dto.setId(item.getId());
            dto.setName(item.getName());
            dto.setDescription(item.getDescription());
            dto.setAvailable(item.getAvailable());
            dto.setOwner(item.getOwner().getId());
            dto.setComments(CommentMapper.toCommentDtoList(item.getComment()));
            fillBookingDates(dto, nextBookingsMap, lastBookingsMap, item.getId(),
                    ownerId, item.getOwner().getId());
            result.add(dto);
        }
        return result;
    }

    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItem(text.toLowerCase()).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public CommentDto addComment(int itemId, int userId, CommentDto commentDto) {
        User author = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("пользователь не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id " + itemId + " не найдена"));
        boolean hasAnyBooking = bookingRepository.existsByBookerIdAndItemId(userId, itemId);
        if (!hasAnyBooking) {
            throw new ValidationException("Вы не брали эту вещь в аренду");
        }
        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(b -> b.getBooker() != null && b.getBooker().getId().equals(userId))
                .filter(b -> b.getItem() != null && b.getItem().getId().equals(itemId))
                .collect(Collectors.toList());
        List<Booking> approvedBookings = bookings.stream()
                .filter(b -> "APPROVED".equals(b.getState()))
                .collect(Collectors.toList());
        if (approvedBookings.isEmpty()) {
            throw new ValidationException("Можно комментировать только подтверждённые аренды");
        }
        boolean hasCompletedApproved = approvedBookings.stream()
                .anyMatch(b -> b.getEndBooking().isBefore(LocalDateTime.now()));
        if (hasCompletedApproved) {
            commentDto.setCreated(LocalDateTime.now());
            Comment newComment = commentRepository.save(CommentMapper.toComment(commentDto, author, item));
            return CommentMapper.toCommentDto(newComment);
        }
        throw new ValidationException("Можно комментировать только после завершения аренды");
    }

    private void fillBookingDates(ItemWithBookingsDto dto,
                                  Map<Integer, Booking> nextBookingsMap,
                                  Map<Integer, Booking> lastBookingsMap,
                                  Integer itemId,
                                  Integer userId,
                                  Integer ownerId) {
        dto.setLastBooking(null);
        dto.setNextBooking(null);
        if (!userId.equals(ownerId)) {
            return;
        }
        if (lastBookingsMap != null && lastBookingsMap.containsKey(itemId)) {
            Booking lastBooking = lastBookingsMap.get(itemId);

            boolean isApproved = "APPROVED".equals(lastBooking.getState());
            boolean isCompleted = lastBooking.getEndBooking().isBefore(LocalDateTime.now());
            boolean isForThisItem = lastBooking.getItem().getId().equals(itemId);
            if (isApproved && isCompleted && isForThisItem) {
                dto.setLastBooking(lastBooking.getStartBooking().toLocalDate());
            }
        }
        if (nextBookingsMap != null && nextBookingsMap.containsKey(itemId)) {
            Booking nextBooking = nextBookingsMap.get(itemId);
            boolean isApproved = "APPROVED".equals(nextBooking.getState());
            boolean isFuture = nextBooking.getStartBooking().isAfter(LocalDateTime.now());
            boolean isForThisItem = nextBooking.getItem().getId().equals(itemId);

            if (isApproved && isFuture && isForThisItem) {
                dto.setNextBooking(nextBooking.getStartBooking().toLocalDate());
            }
        }
    }
}

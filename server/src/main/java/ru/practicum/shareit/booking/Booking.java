package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booker_id")
    private User booker;
    @Column(name = "start_booking")
    private LocalDateTime startBooking;
    @Column(name = "end_booking")
    private LocalDateTime endBooking;
    @Enumerated(EnumType.STRING)  // Сохранится как "WAITING", "APPROVED"...
    @Column(name = "state", length = 20)
    private BookingStatus state;
}
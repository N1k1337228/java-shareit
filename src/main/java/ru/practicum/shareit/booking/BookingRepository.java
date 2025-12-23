package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByBookerId(Integer bookerId);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.startBooking <= ?2 AND b.endBooking > ?2")
    List<Booking> getAllCurrentBookingOnBookerId(int bookerId, LocalDate currentDate);

    List<Booking> findAllByBookerIdAndEndBookingBefore(int bookerId, LocalDate currentDate);

    List<Booking> findAllByBookerIdAndStartBookingAfter(int bookerId, LocalDate currentDate);

    List<Booking> findAllByBookerIdAndState(int bookerId, String state);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.owner.id = ?1")
    List<Booking> getAllBookingOnOwnerId(int ownerId);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.owner.id = ?1 AND b.startBooking <= ?2 AND b.endBooking > ?2")
    List<Booking> getAllCurrentBookingOnOwnerId(int ownerId, LocalDate currentDate);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.owner.id = ?1 AND b.endBooking < ?2")
    List<Booking> getAllPastBookingOnOwnerId(int ownerId, LocalDate currentDate);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.owner.id = ?1 AND b.startBooking > ?2")
    List<Booking> getAllFutureBookingOnOwnerId(int ownerId, LocalDate currentDate);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.owner.id = ?1 AND b.state < ?2")
    List<Booking> getAllBookingOnOwnerIdAndState(int ownerId, String state);

    boolean existsByBookerIdAndItemId(int bookerId, int itemId);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds AND b.startBooking > :now AND b.state = 'APPROVED' ORDER BY b.startBooking ASC")
    List<Booking> findNextBookingsForItems(@Param("itemIds") List<Integer> itemIds, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds AND b.endBooking < :now AND b.state = 'APPROVED' ORDER BY b.endBooking DESC")
    List<Booking> findLastBookingsForItems(@Param("itemIds") List<Integer> itemIds, @Param("now") LocalDateTime now);
}
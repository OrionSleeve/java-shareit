package ru.practicum.shareit.booking.bookingRepository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start < CURRENT_TIMESTAMP " +
            "AND b.end > CURRENT_TIMESTAMP ")
    List<Booking> findAllByBookerIdCurrent(long bookerId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.end < CURRENT_TIMESTAMP ")
    List<Booking> findAllByBookerIdPast(long bookerId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start > CURRENT_TIMESTAMP ")
    List<Booking> findAllByBookerIdFuture(long bookerId, Sort sort);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long userId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start < CURRENT_TIMESTAMP " +
            "AND b.end > CURRENT_TIMESTAMP ")
    List<Booking> findAllByItemOwnerIdCurrentOrderByStartDesc(long ownerId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.end < CURRENT_TIMESTAMP ")
    List<Booking> findAllByItemOwnerIdPastOrderByStartDesc(long ownerId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start > CURRENT_TIMESTAMP ")
    List<Booking> findAllByItemOwnerIdFutureOrderByStartDesc(long ownerId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, Status status);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingDto(b.id, b.booker.id) " +
            "FROM Booking AS b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.status = 'APPROVED' " +
            "AND b.item.id = :itemId " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start ASC ")
    List<BookingDto> findNextClosestBookingByOwnerId(long ownerId, long itemId);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingDto(b.id, b.booker.id) " +
            "FROM Booking AS b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.status = 'APPROVED' " +
            "AND b.item.id = :itemId " +
            "AND b.start < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC ")
    List<BookingDto> findLastClosestBookingByOwnerId(long ownerId, long itemId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.item.id = :itemId " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdAndItemIdPast(long bookerId, long itemId);
}

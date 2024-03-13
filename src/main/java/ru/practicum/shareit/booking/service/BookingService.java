package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoReq;
import ru.practicum.shareit.booking.dto.BookingDtoResp;

import java.util.List;

public interface BookingService {
    BookingDtoResp addBooking(BookingDtoReq bookingDto, long userId);

    BookingDtoResp approveBooking(long userId, long bookingId, boolean approved);

    BookingDtoResp getBookingById(long bookingId, long userId);

    List<BookingDtoResp> getBookingByBookerId(long userId, String state);

    List<BookingDtoResp> getBookingByItemOwner(long userId, String state);
}

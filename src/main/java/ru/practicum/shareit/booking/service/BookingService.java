package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingState;

import java.awt.*;
import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingDto bookingDto, long userId);

    BookingDto approveBooking(long userId, long bookingId, boolean approved);

    BookingDto getBookingById(long userId, long bookingId);

    List<BookingDto> getItemsByBookerId(long userId, BookingState state);

    List<BookingDto> getBookingByItemOwner(long userId, BookingState state);
}

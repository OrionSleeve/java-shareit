package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoReq;
import ru.practicum.shareit.booking.dto.BookingDtoResp;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class BookingMapper {

    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public BookingDtoResp toBookingDto(Booking booking) {
        String startDate = DATE_TIME_FORMATTER.format(booking.getStart());
        String endDate = DATE_TIME_FORMATTER.format(booking.getEnd());

        return BookingDtoResp.builder()
                .id(booking.getId())
                .status(String.valueOf(booking.getStatus()))
                .start(startDate)
                .end(endDate)
                .booker(Optional.ofNullable(booking.getBooker()).map(UserMapper::toUserDto).orElse(null))
                .item(ItemMapper.toItemDto(booking.getItem()))
                .build();
    }

    public List<BookingDtoResp> toBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public Booking fromBookingDtoRequest(BookingDtoReq dto, User booker, Item item) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        return booking;
    }
}

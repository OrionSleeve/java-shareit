package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.bookingRepository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    @Override
    public BookingDto addBooking(BookingDto bookingDto, long userId) {
        Item item = itemRepository.findById(bookingDto.getItemId()).get();
        User user = userRepository.findById(userId).get();

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);

        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto approveBooking(long userId, long bookingId, boolean approved) {
        return null;
    }

    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        return null;
    }

    @Override
    public List<BookingDto> getItemsByBookerId(long userId, BookingState state) {
        return null;
    }

    @Override
    public List<BookingDto> getBookingByItemOwner(long userId, BookingState state) {
        return null;
    }
}

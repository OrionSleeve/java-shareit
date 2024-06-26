package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.bookingRepository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoReq;
import ru.practicum.shareit.booking.dto.BookingDtoResp;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingState;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.exception.InvalidRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.pagination.Paginator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private static final Sort SORT_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingDtoResp addBooking(BookingDtoReq bookingDto, long userId) {
        User booker = checkUser(userId);
        Item item = checkItem(bookingDto.getItemId());

        if (!item.getAvailable())
            throw new InvalidRequestException("Item with id " + bookingDto.getItemId() + " is not available");
        if (item.getOwner().getId().equals(booker.getId()))
            throw new NotFoundException("You cannot reserve your item");

        Booking booking = BookingMapper.fromBookingDtoRequest(bookingDto, booker, item);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDtoResp approveBooking(long userId, long bookingId, boolean approved) {
        checkUser(userId);
        Booking booking = checkBooking(bookingId);

        if (booking.getItem().getOwner().getId() != userId)
            throw new NotFoundException("User with id " + userId + " is not the owner of item");

        Status currentStatus = booking.getStatus();
        if ((currentStatus == Status.APPROVED && approved) || (currentStatus == Status.REJECTED && !approved))
            throw new InvalidRequestException("Booking status changed");

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoResp getBookingById(long userId, long bookingId) {
        checkUser(userId);
        Booking booking = checkBooking(bookingId);
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId)
            throw new NotFoundException("User with id " + userId + " is not the owner / booker of item");
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResp> getBookingByBookerId(long userId, String stateString, int from, int size) {
    Pageable page = Paginator.createPageRequestWithSort(from, size, SORT_START_DESC);
        checkUser(userId);
        BookingState state = BookingState.toState(stateString);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId, page);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdCurrent(userId, page);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdPast(userId, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdFuture(userId, page);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED, page);
                break;
            default:
                throw new InvalidRequestException("Unknown state: " + state);
        }
        return BookingMapper.toBookingDto(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResp> getBookingByItemOwner(long userId, String stateString, int from, int size) {
        Pageable page = Paginator.createPageRequestWithSort(from, size, SORT_START_DESC);
        checkUser(userId);
        BookingState state = BookingState.toState(stateString);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerId(userId, page);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdCurrent(userId, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdFuture(userId, page);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdPast(userId, page);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.REJECTED, page);
                break;
            default:
                throw new InvalidRequestException("Unknown state: " + state);
        }
        return BookingMapper.toBookingDto(bookings);
    }


    private User checkUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    private Item checkItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(("Item with id " + itemId + " not found")));
    }

    private Booking checkBooking(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));
    }
}

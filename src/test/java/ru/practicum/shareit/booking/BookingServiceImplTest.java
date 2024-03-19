package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.bookingRepository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoReq;
import ru.practicum.shareit.booking.dto.BookingDtoResp;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.InvalidRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @Test
    void createBooking_whenValid_thenReturnBooking() {
        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long userId = 2L;
        booker.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(true);

        BookingDtoReq request = new BookingDtoReq();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking = BookingMapper.fromBookingDtoRequest(request, booker, item);
        booking.setStatus(Status.WAITING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDtoResp expect = BookingMapper.toBookingDto(booking);
        BookingDtoResp actual = bookingService.addBooking(request, userId);
        assertEquals(expect, actual);
    }

    @Test
    void createBooking_whenBookerIdEqualsOwnerId_thenBookingNotFoundException() {
        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long userId = 1L;
        booker.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(true);

        BookingDtoReq request = new BookingDtoReq();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking = BookingMapper.fromBookingDtoRequest(request, booker, item);
        booking.setStatus(Status.WAITING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(request, userId));
    }

    @Test
    void updateBooking_whenValid_thenReturnBooking() {
        Boolean approved = false;

        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long userId = 2L;
        booker.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(true);

        BookingDtoReq request = new BookingDtoReq();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking = BookingMapper.fromBookingDtoRequest(request, booker, item);
        long bookingId = 1L;
        booking.setId(bookingId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDtoResp expect = BookingMapper.toBookingDto(booking);
        expect.setStatus("REJECTED");
        BookingDtoResp actual = bookingService.approveBooking(ownerId, bookingId, approved);
        assertEquals(expect, actual);
    }

    @Test
    void updateBooking_whenUserIsNotOwner_thenNotFoundException() {
        Boolean approved = true;

        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long userId = 2L;
        booker.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(true);

        BookingDtoReq request = new BookingDtoReq();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking = BookingMapper.fromBookingDtoRequest(request, booker, item);
        long bookingId = 1L;
        booking.setId(bookingId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(userId, bookingId, approved));
    }

    @Test
    void updateBooking_whenStatusAlreadyChanged_thenInvalidException() {
        Boolean approved = true;

        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long userId = 2L;
        booker.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(true);

        BookingDtoReq request = new BookingDtoReq();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking = BookingMapper.fromBookingDtoRequest(request, booker, item);
        long bookingId = 1L;
        booking.setId(bookingId);
        booking.setStatus(Status.APPROVED);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(InvalidRequestException.class, () -> bookingService.approveBooking(ownerId, bookingId, approved));
    }

    @Test
    void getBookingsByUserByState_whenValid_thenReturnBookingsList() {
        int from = 0, size = 10;
        Pageable page = PageRequest.of(from / size, size, Sort.by("start").descending());

        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long bookerId = 2L;
        booker.setId(bookerId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(true);

        BookingDtoReq request1 = new BookingDtoReq();
        request1.setItemId(itemId);
        request1.setStart(LocalDateTime.now());
        request1.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking1 = BookingMapper.fromBookingDtoRequest(request1, booker, item);
        long bookingId1 = 1L;
        booking1.setId(bookingId1);
        booking1.setStatus(Status.APPROVED);

        BookingDtoReq request2 = new BookingDtoReq();
        request2.setItemId(itemId);
        request2.setStart(LocalDateTime.now());
        request2.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking2 = BookingMapper.fromBookingDtoRequest(request2, booker, item);
        long bookingId2 = 2L;
        booking2.setId(bookingId2);
        booking2.setStatus(Status.REJECTED);

        BookingDtoReq request3 = new BookingDtoReq();
        request3.setItemId(itemId);
        request3.setStart(LocalDateTime.now());
        request3.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking3 = BookingMapper.fromBookingDtoRequest(request3, booker, item);
        long bookingId3 = 3L;
        booking3.setId(bookingId3);
        booking3.setStatus(Status.WAITING);

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, page))
                .thenReturn(List.of(booking1, booking2, booking3));
        when(bookingRepository.findAllByBookerIdCurrent(bookerId, page))
                .thenReturn(List.of(booking1, booking2, booking3));
        when(bookingRepository.findAllByBookerIdPast(bookerId, page))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByBookerIdFuture(bookerId, page))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.REJECTED, page))
                .thenReturn(List.of(booking3));

        List<BookingDtoResp> expectedList1 = BookingMapper.toBookingDto(List.of(booking1, booking2, booking3));
        List<BookingDtoResp> actualList1 = bookingService.getBookingByBookerId(bookerId, "ALL", from, size);
        assertEquals(expectedList1, actualList1);

        List<BookingDtoResp> expectedList2 = BookingMapper.toBookingDto(List.of(booking1, booking2, booking3));
        List<BookingDtoResp> actualList2 = bookingService.getBookingByBookerId(bookerId, "CURRENT", from, size);
        assertEquals(expectedList2, actualList2);

        List<BookingDtoResp> expectedList3 = BookingMapper.toBookingDto(Collections.emptyList());
        List<BookingDtoResp> actualList3 = bookingService.getBookingByBookerId(bookerId, "FUTURE", from, size);
        assertEquals(expectedList3, actualList3);

        List<BookingDtoResp> expectedList4 = BookingMapper.toBookingDto(Collections.emptyList());
        List<BookingDtoResp> actualList4 = bookingService.getBookingByBookerId(bookerId, "PAST", from, size);
        assertEquals(expectedList4, actualList4);

        List<BookingDtoResp> expectedList5 = BookingMapper.toBookingDto(List.of(booking3));
        List<BookingDtoResp> actualList5 = bookingService.getBookingByBookerId(bookerId, "REJECTED", from, size);
        assertEquals(expectedList5, actualList5);

        assertThrows(InvalidRequestException.class,
                () -> bookingService.getBookingByBookerId(bookerId, "UNSUPPORTED", from, size));
    }

    @Test
    void getOwnerItemsBooked_whenValid_thenReturnBookingsList() {
        int from = 0, size = 10;
        Pageable page = PageRequest.of(from / size, size, Sort.by("start").descending());

        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long bookerId = 2L;
        booker.setId(bookerId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(true);

        BookingDtoReq request1 = new BookingDtoReq();
        request1.setItemId(itemId);
        request1.setStart(LocalDateTime.now());
        request1.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking1 = BookingMapper.fromBookingDtoRequest(request1, booker, item);
        long bookingId1 = 1L;
        booking1.setId(bookingId1);
        booking1.setStatus(Status.APPROVED);

        BookingDtoReq request2 = new BookingDtoReq();
        request2.setItemId(itemId);
        request2.setStart(LocalDateTime.now());
        request2.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking2 = BookingMapper.fromBookingDtoRequest(request2, booker, item);
        long bookingId2 = 2L;
        booking2.setId(bookingId2);
        booking2.setStatus(Status.REJECTED);

        BookingDtoReq request3 = new BookingDtoReq();
        request3.setItemId(itemId);
        request3.setStart(LocalDateTime.now());
        request3.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking3 = BookingMapper.fromBookingDtoRequest(request3, booker, item);
        long bookingId3 = 3L;
        booking3.setId(bookingId3);
        booking3.setStatus(Status.WAITING);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerId(ownerId, page))
                .thenReturn(List.of(booking1, booking2, booking3));
        when(bookingRepository.findAllByItemOwnerIdCurrent(ownerId, page))
                .thenReturn(List.of(booking1, booking2, booking3));
        when(bookingRepository.findAllByItemOwnerIdPast(ownerId, page))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemOwnerIdFuture(ownerId, page))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, Status.REJECTED, page))
                .thenReturn(List.of(booking3));

        List<BookingDtoResp> expectedList1 = BookingMapper.toBookingDto(List.of(booking1, booking2, booking3));
        List<BookingDtoResp> actualList1 = bookingService.getBookingByItemOwner(ownerId, "ALL", from, size);
        assertEquals(expectedList1, actualList1);

        List<BookingDtoResp> expectedList2 = BookingMapper.toBookingDto(List.of(booking1, booking2, booking3));
        List<BookingDtoResp> actualList2 = bookingService.getBookingByItemOwner(ownerId, "CURRENT", from, size);
        assertEquals(expectedList2, actualList2);

        List<BookingDtoResp> expectedList3 = BookingMapper.toBookingDto(Collections.emptyList());
        List<BookingDtoResp> actualList3 = bookingService.getBookingByItemOwner(ownerId, "FUTURE", from, size);
        assertEquals(expectedList3, actualList3);

        List<BookingDtoResp> expectedList4 = BookingMapper.toBookingDto(Collections.emptyList());
        List<BookingDtoResp> actualList4 = bookingService.getBookingByItemOwner(ownerId, "PAST", from, size);
        assertEquals(expectedList4, actualList4);

        List<BookingDtoResp> expectedList5 = BookingMapper.toBookingDto(List.of(booking3));
        List<BookingDtoResp> actualList5 = bookingService.getBookingByItemOwner(ownerId, "REJECTED", from, size);
        assertEquals(expectedList5, actualList5);

        assertThrows(InvalidRequestException.class,
                () -> bookingService.getBookingByItemOwner(ownerId, "UNSUPPORTED", from, size));
    }

    @Test
    void getBookingById_whenValid_thenReturnBooking() {
        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long userId = 1L;
        booker.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(true);

        BookingDtoReq request = new BookingDtoReq();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking = BookingMapper.fromBookingDtoRequest(request, booker, item);
        long bookingId = 1L;
        booking.setId(bookingId);
        booking.setStatus(Status.WAITING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDtoResp expect = BookingMapper.toBookingDto(booking);
        BookingDtoResp actual = bookingService.getBookingById(bookingId, userId);
        assertEquals(expect, actual);
    }
}
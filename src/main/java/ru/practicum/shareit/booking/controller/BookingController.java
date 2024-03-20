package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoReq;
import ru.practicum.shareit.booking.dto.BookingDtoResp;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.Constants.HEADER;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResp createBooking(@RequestBody @Valid BookingDtoReq bookingDto,
                                        @RequestHeader(HEADER) long userId) {
        log.info("Received request to create booking for user {}", userId);
        BookingDtoResp response = bookingService.addBooking(bookingDto, userId);
        log.info("Created booking successfully for user {}", userId);
        return response;
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResp approveBooking(@PathVariable long bookingId,
                                         @RequestParam Boolean approved,
                                         @RequestHeader(HEADER) long userId) {
        log.info("Received request to update booking {} by user {}", bookingId, userId);
        BookingDtoResp response = bookingService.approveBooking(userId, bookingId, approved);
        log.info("Updated booking {} successfully by user {}", bookingId, userId);
        return response;
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResp getBookingById(@PathVariable long bookingId,
                                         @RequestHeader(HEADER) long userId) {
        log.info("Received request to fetch booking {} by user {}", bookingId, userId);
        BookingDtoResp response = bookingService.getBookingById(userId, bookingId);
        log.info("Fetched booking {} successfully by user {}", bookingId, userId);
        return response;
    }

    @GetMapping
    public List<BookingDtoResp> getBookingsByUser(@RequestParam(defaultValue = "ALL") String state,
                                                  @RequestHeader(HEADER) long userId,
                                                  @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(name = "size", defaultValue = "10") @Min(0) int size) {
        log.info("Received request to fetch bookings for user {} with state {}", userId, state);
        List<BookingDtoResp> response = bookingService.getBookingByBookerId(userId, state, from, size);
        log.info("Fetched bookings successfully for user {} with state {}", userId, state);
        return response;
    }

    @GetMapping("/owner")
    public List<BookingDtoResp> getOwnerItemsBooking(@RequestParam(defaultValue = "ALL") String state,
                                                     @RequestHeader(HEADER) long userId,
                                                     @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                     @RequestParam(name = "size", defaultValue = "10") @Min(0) int size) {
        log.info("Received request to fetch bookings for item owner {} with state {}", userId, state);
        List<BookingDtoResp> response = bookingService.getBookingByItemOwner(userId, state, from, size);
        log.info("Fetched bookings successfully for item owner {} with state {}", userId, state);
        return response;
    }
}

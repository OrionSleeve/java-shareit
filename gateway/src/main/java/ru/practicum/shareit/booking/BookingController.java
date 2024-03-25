package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoReq;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.Constants.HEADER;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody @Valid BookingDtoReq bookingDto,
                                                @RequestHeader(HEADER) long userId) {
        log.info("Created booking successfully for user {}", userId);
        return bookingClient.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable long bookingId,
                                                 @RequestParam Boolean approved,
                                                 @RequestHeader(HEADER) long userId) {
        log.info("Updated booking {} successfully by user {}", bookingId, userId);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable long bookingId,
                                                 @RequestHeader(HEADER) long userId) {
        log.info("Fetched booking {} successfully by user {}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);

    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUser(@RequestParam(defaultValue = "ALL") String state,
                                                    @RequestHeader(HEADER) long userId,
                                                    @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                    @RequestParam(name = "size", defaultValue = "10") @Min(0) int size) {
        BookingState bookingState = BookingState.toState(state);
        log.info("Fetched bookings successfully for user {} with state {}", userId, state);
        return bookingClient.getBookingByBookerId(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerItemsBooking(@RequestParam(defaultValue = "ALL") String state,
                                                       @RequestHeader(HEADER) long userId,
                                                       @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                       @RequestParam(name = "size", defaultValue = "10") @Min(0) int size) {
        BookingState bookingState = BookingState.toState(state);
        log.info("Fetched bookings successfully for item owner {} with state {}", userId, state);
        return bookingClient.getBookingByItemOwner(userId, bookingState, from, size);
    }
}

package ru.practicum.shareit.booking.model.enums;

import ru.practicum.shareit.exception.InvalidRequestException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState toState(String state) {
        for (BookingState bookingState : values()) {
            if (bookingState.name().equalsIgnoreCase(state)) {
                return bookingState;
            }
        }
        throw new InvalidRequestException("Unknown state: " + state);
    }
}

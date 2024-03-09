package ru.practicum.shareit.booking.dateTimeValidator;

import ru.practicum.shareit.booking.dateTimeValidator.DateTimeRange;
import ru.practicum.shareit.booking.dto.BookingDtoReq;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DateTimeRangeValidator implements ConstraintValidator<DateTimeRange, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (!(value instanceof BookingDtoReq)) {
            return false;
        }

        BookingDtoReq entity = (BookingDtoReq) value;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = entity.getStart();
        LocalDateTime end = entity.getEnd();

        return start != null && end != null && end.isAfter(start) && start.isAfter(now);
    }
}

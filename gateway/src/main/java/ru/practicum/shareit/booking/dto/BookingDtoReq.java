package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Data;
import ru.practicum.shareit.booking.dateTimeValidator.DateTimeRange;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@DateTimeRange
public class BookingDtoReq {
    @NotNull
    private long itemId;
    @NotNull
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime start;
    @NotNull
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime end;
}

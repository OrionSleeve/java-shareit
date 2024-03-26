package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingDtoReq;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    public void testSerialize() {
        BookingDtoReq bookingDto = new BookingDtoReq();
        bookingDto.setItemId(123);
        bookingDto.setStart(LocalDateTime.of(2024, 6, 7, 10, 0));
        bookingDto.setEnd(LocalDateTime.of(2024, 6, 7, 12, 0));

        String json = objectMapper.writeValueAsString(bookingDto);

        String expectedJson = "{\"itemId\":123,\"start\":\"2024-06-07T10:00:00\",\"end\":\"2024-06-07T12:00:00\"}";
        assertEquals(expectedJson, json);
    }

    @Test
    @SneakyThrows
    public void testDeserialize() {
        String json = "{\"itemId\":123,\"start\":\"2024-06-07T10:00:00\",\"end\":\"2024-06-07T12:00:00\"}";

        BookingDtoReq bookingDto = objectMapper.readValue(json, BookingDtoReq.class);

        assertEquals(123, bookingDto.getItemId());
        assertEquals(LocalDateTime.of(2024, 6, 7, 10, 0), bookingDto.getStart());
        assertEquals(LocalDateTime.of(2024, 6, 7, 12, 0), bookingDto.getEnd());
    }
}

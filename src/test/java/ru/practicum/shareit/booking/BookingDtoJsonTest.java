package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingDtoReq;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator;

    public BookingDtoJsonTest() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

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


    @Test
    public void testValidation() {
        BookingDtoReq bookingDto = new BookingDtoReq();
        bookingDto.setItemId(123);
        bookingDto.setStart(null);
        bookingDto.setEnd(null);

        Set<ConstraintViolation<BookingDtoReq>> violations = validator.validate(bookingDto);
        assertEquals(3, violations.size());

        ConstraintViolation<BookingDtoReq> startViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("start"))
                .findFirst()
                .orElse(null);
        assertNotNull(startViolation);
        assertEquals("must be not null", startViolation.getMessage());

        ConstraintViolation<BookingDtoReq> endViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("end"))
                .findFirst()
                .orElse(null);
        assertNotNull(endViolation);
        assertEquals("must be not null", endViolation.getMessage());
    }
}

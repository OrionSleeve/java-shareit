package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.CrudTestUtils;
import ru.practicum.shareit.booking.dto.BookingDtoReq;
import ru.practicum.shareit.booking.dto.BookingDtoResp;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.exception.InvalidRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constants.HEADER;

@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingControllerTest extends CrudTestUtils {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final Validator VALIDATOR;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void testValidations() {
        BookingDtoReq invalidBooking = new BookingDtoReq();
        invalidBooking.setStart(LocalDateTime.of(800, 1, 1, 12, 0, 0));
        invalidBooking.setEnd(LocalDateTime.of(900, 1, 1, 12, 0, 0));
        Set<ConstraintViolation<BookingDtoReq>> validates = VALIDATOR.validate(invalidBooking);
        assertTrue(validates.size() > 0);
    }

    @Test
    void createBooking_whenBookingIdIncorrect_thenBookingNotFoundException() throws Exception {
        UserDto ownerDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        long ownerId = createUser(ownerDto).getId();

        ItemDto itemDto = ItemDto.builder().name("Item").description("Description").available(true).build();
        long itemId = createItem(itemDto, ownerId).getId();

        BookingDtoReq request = new BookingDtoReq();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.of(2024, 10, 1, 9, 0, 30));
        request.setEnd(LocalDateTime.of(2024, 10, 2, 9, 0, 30));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, String.valueOf(ownerId)))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof NotFoundException));
    }

    @Test
    void createBooking_whenItemNotAvailable_thenItemNotFoundException() throws Exception {
        UserDto ownerDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        long ownerId = createUser(ownerDto).getId();

        ItemDto itemDto = ItemDto.builder().name("Item").description("Description").available(false).build();
        long itemId = createItem(itemDto, ownerId).getId();

        BookingDtoReq request = new BookingDtoReq();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.of(2024, 10, 1, 9, 0, 30));
        request.setEnd(LocalDateTime.of(2024, 10, 2, 9, 0, 30));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, String.valueOf(ownerId)))
                .andExpect(result -> assertFalse(result.getResolvedException() instanceof NotFoundException));
    }

    @Test
    void getBooking_whenValid_thenReturnBooking() throws Exception {
        UserDto ownerDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        long ownerId = createUser(ownerDto).getId();

        UserDto bookerDto = UserDto.builder().name("Toma").email("toma@email.com").build();
        long bookerId = createUser(bookerDto).getId();

        ItemDto itemDto = ItemDto.builder().name("Item").description("Description").available(true).build();
        long itemId = createItem(itemDto, ownerId).getId();

        BookingDtoReq request = new BookingDtoReq();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.of(2024, 10, 1, 9, 0, 30));
        request.setEnd(LocalDateTime.of(2024, 10, 2, 9, 0, 30));

        BookingDtoResp response = createBooking(request, bookerId);

        mockMvc.perform(get("/bookings/{bookingId}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, String.valueOf(bookerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.start").value(response.getStart()))
                .andExpect(jsonPath("$.end").value(response.getEnd()))
                .andExpect(jsonPath("$.booker.id").value(response.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(response.getItem().getId()))
                .andReturn();
    }

    @Test
    void getBooking_whenOtherUser_thenNotFoundException() throws Exception {
        UserDto ownerDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        long ownerId = createUser(ownerDto).getId();

        UserDto bookerDto = UserDto.builder().name("Toma").email("toma@email.com").build();
        long bookerId = createUser(bookerDto).getId();

        ItemDto itemDto = ItemDto.builder().name("Item").description("Description").available(true).build();
        long itemId = createItem(itemDto, ownerId).getId();

        long otherId = createUser(UserDto.builder().name("otherName").email("other@email.com").build()).getId();

        BookingDtoReq request = new BookingDtoReq();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.of(2024, 10, 1, 9, 0, 30));
        request.setEnd(LocalDateTime.of(2024, 10, 2, 9, 0, 30));

        BookingDtoResp response = createBooking(request, bookerId);

        mockMvc.perform(get("/bookings/{bookingId}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, String.valueOf(otherId)))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }

    @Test
    void updateBooking_whenValid_thenReturnUpdatedBooking() throws Exception {
        UserDto ownerDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        long ownerId = createUser(ownerDto).getId();

        UserDto bookerDto = UserDto.builder().name("Toma").email("toma@email.com").build();
        long bookerId = createUser(bookerDto).getId();

        ItemDto itemDto = ItemDto.builder().name("Item").description("Description").available(true).build();
        long itemId = createItem(itemDto, ownerId).getId();

        BookingDtoReq request = new BookingDtoReq();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.of(2024, 10, 1, 9, 0, 30));
        request.setEnd(LocalDateTime.of(2024, 10, 2, 9, 0, 30));

        BookingDtoResp response = createBooking(request, bookerId);

        Boolean approved = false;

        mockMvc.perform(patch("/bookings/{bookingId}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved))
                        .header(HEADER, String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.status").value(String.valueOf(Status.REJECTED)))
                .andReturn();

        mockMvc.perform(patch("/bookings/{bookingId}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved))
                        .header(HEADER, String.valueOf(ownerId)))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidRequestException));
    }

    @Test
    void updateBooking_whenOtherUser_thenNotFoundException() throws Exception {
        UserDto ownerDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        long ownerId = createUser(ownerDto).getId();

        UserDto bookerDto = UserDto.builder().name("Toma").email("toma@email.com").build();
        long bookerId = createUser(bookerDto).getId();

        ItemDto itemDto = ItemDto.builder().name("Item").description("Description").available(true).build();
        long itemId = createItem(itemDto, ownerId).getId();

        BookingDtoReq request = new BookingDtoReq();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.of(2024, 10, 1, 9, 0, 30));
        request.setEnd(LocalDateTime.of(2024, 10, 2, 9, 0, 30));

        BookingDtoResp response = createBooking(request, bookerId);

        Boolean approved = false;

        long otherId = createUser(UserDto.builder().name("otherName").email("other@email.com").build()).getId();

        mockMvc.perform(patch("/bookings/{bookingId}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved))
                        .header(HEADER, String.valueOf(otherId)))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }

    @Test
    void getBookingsByUserByState_whenValid_thenReturnBookingsList() throws Exception {
        UserDto ownerDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        long ownerId = createUser(ownerDto).getId();

        UserDto bookerDto = UserDto.builder().name("Toma").email("toma@email.com").build();
        long bookerId = createUser(bookerDto).getId();

        ItemDto itemDto = ItemDto.builder().name("Item").description("Description").available(true).build();
        long itemId = createItem(itemDto, ownerId).getId();

        BookingDtoReq request1 = new BookingDtoReq();
        request1.setItemId(itemId);
        request1.setStart(LocalDateTime.of(2024, 10, 1, 9, 0, 30));
        request1.setEnd(LocalDateTime.of(2024, 10, 2, 9, 0, 30));

        BookingDtoReq request2 = new BookingDtoReq();
        request2.setItemId(itemId);
        request2.setStart(LocalDateTime.of(2024, 10, 1, 9, 0, 30));
        request2.setEnd(LocalDateTime.of(2024, 10, 2, 9, 0, 30));

        BookingDtoReq request3 = new BookingDtoReq();
        request3.setItemId(itemId);
        request3.setStart(LocalDateTime.of(2024, 10, 1, 9, 0, 30));
        request3.setEnd(LocalDateTime.of(2024, 10, 2, 9, 0, 30));

        BookingDtoResp response1 = createBooking(request1, bookerId);
        BookingDtoResp response2 = createBooking(request2, bookerId);
        BookingDtoResp response3 = createBooking(request3, bookerId);

        Boolean approved1 = true;
        Boolean approved2 = false;
        Boolean approved3 = false;

        mockMvc.perform(patch("/bookings/{bookingId}", response1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved1))
                        .header(HEADER, String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response1.getId()))
                .andExpect(jsonPath("$.status").value(String.valueOf(Status.APPROVED)))
                .andReturn();

        response1.setStatus("APPROVED");

        mockMvc.perform(patch("/bookings/{bookingId}", response2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved2))
                        .header(HEADER, String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response2.getId()))
                .andExpect(jsonPath("$.status").value(String.valueOf(Status.REJECTED)))
                .andReturn();

        response2.setStatus("REJECTED");

        mockMvc.perform(patch("/bookings/{bookingId}", response3.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved3))
                        .header(HEADER, String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response3.getId()))
                .andExpect(jsonPath("$.status").value(String.valueOf(Status.REJECTED)))
                .andReturn();

        response3.setStatus("REJECTED");

        List<BookingDtoResp> expectedList1 = new ArrayList<>();
        expectedList1.add(response1);
        expectedList1.add(response2);
        expectedList1.add(response3);

        List<BookingDtoResp> expectedList2 = Collections.emptyList();

        List<BookingDtoResp> expectedList3 = Collections.emptyList();

        List<BookingDtoResp> expectedList4 = new ArrayList<>(expectedList1);

        List<BookingDtoResp> expectedList5 = new ArrayList<>();
        expectedList5.add(response2);
        expectedList5.add(response3);

        int from = 0, size = 10;

        MvcResult result1 = this.mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(HEADER, String.valueOf(bookerId))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResp> actualList1 = List.of(objectMapper.readValue(result1
                        .getResponse()
                        .getContentAsString(),
                BookingDtoResp[].class));

        MvcResult result2 = this.mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "CURRENT")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(HEADER, String.valueOf(bookerId))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResp> actualList2 = List.of(objectMapper.readValue(result2
                        .getResponse()
                        .getContentAsString(),
                BookingDtoResp[].class));

        MvcResult result3 = this.mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "PAST")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(HEADER, String.valueOf(bookerId))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResp> actualList3 = List.of(objectMapper.readValue(result3
                        .getResponse()
                        .getContentAsString(),
                BookingDtoResp[].class));

        MvcResult result4 = this.mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "FUTURE")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(HEADER, String.valueOf(bookerId))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResp> actualList4 = List.of(objectMapper.readValue(result4
                        .getResponse()
                        .getContentAsString(),
                BookingDtoResp[].class));

        MvcResult result5 = this.mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "REJECTED")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(HEADER, String.valueOf(bookerId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResp> actualList5 = List.of(objectMapper.readValue(result5
                        .getResponse()
                        .getContentAsString(),
                BookingDtoResp[].class));

        assertEquals(expectedList1, actualList1);
        assertEquals(expectedList2, actualList2);
        assertEquals(expectedList3, actualList3);
        assertEquals(expectedList4, actualList4);
        assertEquals(expectedList5, actualList5);

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "UNSUPPORTED")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(HEADER, String.valueOf(bookerId)))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidRequestException));
    }

    @Test
    void getOwnerItemsBooked_whenValid_theReturnBookingsList() throws Exception {
        UserDto ownerDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        long ownerId = createUser(ownerDto).getId();

        UserDto bookerDto = UserDto.builder().name("Toma").email("toma@email.com").build();
        long bookerId = createUser(bookerDto).getId();

        ItemDto itemDto = ItemDto.builder().name("Item").description("Description").available(true).build();
        long itemId = createItem(itemDto, ownerId).getId();

        BookingDtoReq request1 = new BookingDtoReq();
        request1.setItemId(itemId);
        request1.setStart(LocalDateTime.of(2024, 10, 1, 9, 0, 30));
        request1.setEnd(LocalDateTime.of(2024, 10, 2, 9, 0, 30));

        BookingDtoReq request2 = new BookingDtoReq();
        request2.setItemId(itemId);
        request2.setStart(LocalDateTime.of(2024, 10, 1, 9, 0, 30));
        request2.setEnd(LocalDateTime.of(2024, 10, 2, 9, 0, 30));

        BookingDtoReq request3 = new BookingDtoReq();
        request3.setItemId(itemId);
        request3.setStart(LocalDateTime.of(2024, 10, 1, 9, 0, 30));
        request3.setEnd(LocalDateTime.of(2024, 10, 2, 9, 0, 30));

        BookingDtoResp response1 = createBooking(request1, bookerId);
        BookingDtoResp response2 = createBooking(request2, bookerId);
        BookingDtoResp response3 = createBooking(request3, bookerId);

        Boolean approved1 = true;
        Boolean approved2 = false;
        Boolean approved3 = false;

        mockMvc.perform(patch("/bookings/{bookingId}", response1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved1))
                        .header(HEADER, String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response1.getId()))
                .andExpect(jsonPath("$.status").value(String.valueOf(Status.APPROVED)))
                .andReturn();

        response1.setStatus("APPROVED");

        mockMvc.perform(patch("/bookings/{bookingId}", response2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved2))
                        .header(HEADER, String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response2.getId()))
                .andExpect(jsonPath("$.status").value(String.valueOf(Status.REJECTED)))
                .andReturn();

        response2.setStatus("REJECTED");

        mockMvc.perform(patch("/bookings/{bookingId}", response3.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved3))
                        .header(HEADER, String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response3.getId()))
                .andExpect(jsonPath("$.status").value(String.valueOf(Status.REJECTED)))
                .andReturn();

        response3.setStatus("REJECTED");

        List<BookingDtoResp> expectedList1 = new ArrayList<>();
        expectedList1.add(response1);
        expectedList1.add(response2);
        expectedList1.add(response3);

        List<BookingDtoResp> expectedList2 = Collections.emptyList();

        List<BookingDtoResp> expectedList3 = Collections.emptyList();

        List<BookingDtoResp> expectedList4 = new ArrayList<>(expectedList1);

        List<BookingDtoResp> expectedList5 = new ArrayList<>();
        expectedList5.add(response2);
        expectedList5.add(response3);

        int from = 0, size = 10;

        MvcResult result1 = this.mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(HEADER, String.valueOf(ownerId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResp> actualList1 = List.of(objectMapper.readValue(result1
                        .getResponse()
                        .getContentAsString(),
                BookingDtoResp[].class));

        MvcResult result2 = this.mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "CURRENT")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(HEADER, String.valueOf(ownerId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResp> actualList2 = List.of(objectMapper.readValue(result2
                        .getResponse()
                        .getContentAsString(),
                BookingDtoResp[].class));

        MvcResult result3 = this.mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "PAST")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(HEADER, String.valueOf(ownerId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResp> actualList3 = List.of(objectMapper.readValue(result3
                        .getResponse()
                        .getContentAsString(),
                BookingDtoResp[].class));

        MvcResult result4 = this.mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "FUTURE")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(HEADER, String.valueOf(ownerId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResp> actualList4 = List.of(objectMapper.readValue(result4
                        .getResponse()
                        .getContentAsString(),
                BookingDtoResp[].class));

        MvcResult result5 = this.mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "REJECTED")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(HEADER, String.valueOf(ownerId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResp> actualList5 = List.of(objectMapper.readValue(result5
                        .getResponse()
                        .getContentAsString(),
                BookingDtoResp[].class));

        assertEquals(expectedList1, actualList1);
        assertEquals(expectedList2, actualList2);
        assertEquals(expectedList3, actualList3);
        assertEquals(expectedList4, actualList4);
        assertEquals(expectedList5, actualList5);

        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "UNSUPPORTED")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(HEADER, String.valueOf(ownerId)))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidRequestException));
    }

    @Test
    void createBooking_whenInvalidBookingRequest_thenBadRequest() throws Exception {
        BookingDtoReq invalidBooking = new BookingDtoReq();
        invalidBooking.setStart(LocalDateTime.of(800, 1, 1, 12, 0, 0));
        invalidBooking.setEnd(LocalDateTime.of(900, 1, 1, 12, 0, 0));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(invalidBooking))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, "1"))
                .andExpect(status().isBadRequest());
    }
}

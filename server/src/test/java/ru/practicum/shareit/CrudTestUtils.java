package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.dto.BookingDtoReq;
import ru.practicum.shareit.booking.dto.BookingDtoResp;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemDescriptionRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constants.HEADER;

@SpringBootTest
@AutoConfigureMockMvc
public class CrudTestUtils {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public UserDto createUser(UserDto userDto) throws Exception {
        MvcResult result = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail())).andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
    }

    public ItemDto createItem(ItemDto itemDto, long ownerId) throws Exception {
        MvcResult result = mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(HEADER, String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable())).andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), ItemDto.class);
    }

    public BookingDtoResp createBooking(BookingDtoReq bookingDto, long userId) throws Exception {
        MvcResult result = mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header(HEADER, String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").value(String.valueOf(bookingDto.getStart())))
                .andExpect(jsonPath("$.end").value(String.valueOf(bookingDto.getEnd())))
                .andExpect(jsonPath("$.status").value("WAITING")).andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), BookingDtoResp.class);
    }

    public ItemRequestDto createItemRequest(ItemDescriptionRequestDto requestDescription, long userId) throws Exception {
        MvcResult result = mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestDescription))
                        .contentType("application/json").header(HEADER, String.valueOf(userId)))
                .andExpect(status().isOk()).andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), ItemRequestDto.class);
    }

}

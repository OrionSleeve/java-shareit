package ru.practicum.shareit.itemRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.CrudTestUtils;
import ru.practicum.shareit.request.dto.ItemDescriptionRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constants.HEADER;

@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestControllerTest extends CrudTestUtils {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void getUserItemRequests() {
        long userId = createUser(UserDto.builder().name("Mark").email("mark@email.com").build()).getId();
        ItemDescriptionRequestDto itemRequestDescription = new ItemDescriptionRequestDto();
        itemRequestDescription.setDescription("description");

        ItemRequestDto itemRequestInfo = createItemRequest(itemRequestDescription, userId);
        ItemRequestDto itemRequestInfoWithItems = ItemRequestDto.builder().id(itemRequestInfo.getId())
                .description(itemRequestInfo.getDescription())
                .created(itemRequestInfo.getCreated()).items(Collections.emptyList()).build();

        List<ItemRequestDto> expected = new ArrayList<>(List.of(itemRequestInfoWithItems));

        MvcResult result = mockMvc.perform(get("/requests").contentType(MediaType.APPLICATION_JSON).header(HEADER, String.valueOf(userId))).andExpect(status().isOk()).andDo(print()).andReturn();

        List<ItemRequestDto> actual = List.of(objectMapper.readValue(result.getResponse().getContentAsString(), ItemRequestDto[].class));

        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void getOtherUsersItemRequests() {
        long userId = createUser(UserDto.builder().name("Mark").email("mark@email.com").build()).getId();
        long otherId = createUser(UserDto.builder().name("Toma").email("Toma@email.com").build()).getId();
        ItemDescriptionRequestDto itemRequestDescription = new ItemDescriptionRequestDto();
        itemRequestDescription.setDescription("description");

        ItemRequestDto itemRequestInfo = createItemRequest(itemRequestDescription, userId);
        ItemRequestDto itemRequestInfoWithItems = ItemRequestDto.builder().id(itemRequestInfo.getId())
                .description(itemRequestInfo.getDescription())
                .created(itemRequestInfo.getCreated())
                .items(Collections.emptyList()).build();

        List<ItemRequestDto> expected = new ArrayList<>(List.of(itemRequestInfoWithItems));

        MvcResult result = mockMvc.perform(get("/requests/all").contentType(MediaType.APPLICATION_JSON).header(HEADER, String.valueOf(otherId))).andExpect(status().isOk()).andDo(print()).andReturn();

        List<ItemRequestDto> actual = List.of(objectMapper.readValue(result.getResponse().getContentAsString(), ItemRequestDto[].class));

        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void getItemRequestById() {
        long userId = createUser(UserDto.builder().name("Mark").email("mark@email.com").build()).getId();
        ItemDescriptionRequestDto itemRequestDescription = new ItemDescriptionRequestDto();
        itemRequestDescription.setDescription("description");

        ItemRequestDto itemRequestInfo = createItemRequest(itemRequestDescription, userId);
        ItemRequestDto expected = ItemRequestDto.builder().id(itemRequestInfo.getId())
                .description(itemRequestInfo.getDescription())
                .created(itemRequestInfo.getCreated())
                .items(Collections.emptyList()).build();

        MvcResult result = mockMvc.perform(get("/requests/{requestId}", expected.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, String.valueOf(userId)))
                .andExpect(status().isOk()).andDo(print()).andReturn();

        ItemRequestDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), ItemRequestDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void createItemRequest_InvalidDescription() {
        long userId = createUser(UserDto.builder().name("Mark").email("mark@email.com").build()).getId();
        ItemDescriptionRequestDto itemRequestDescription = new ItemDescriptionRequestDto();

        MvcResult result = mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(itemRequestDescription)))
                .andExpect(status().isBadRequest()).andDo(print()).andReturn();
    }
}

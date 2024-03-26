package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSerialize() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .ownerId(100L)
                .requestId(200L)
                .nextBooking(new BookingDto())
                .lastBooking(new BookingDto())
                .comments(Collections.emptyList())
                .build();

        String json = objectMapper.writeValueAsString(itemDto);

        String expectedJson = "{\"id\":1," +
                "\"name\":\"Item Name\"," +
                "\"description\":\"Item Description\"," +
                "\"available\":true," +
                "\"ownerId\":100," +
                "\"requestId\":200," +
                "\"nextBooking\":{" +
                "\"id\":null," +
                "\"bookerId\":null}," +
                "\"lastBooking\":{" +
                "\"id\":null," +
                "\"bookerId\":null}," +
                "\"comments\":[]}";
        assertEquals(expectedJson, json);
    }

    @Test
    @SneakyThrows
    public void testDeserialize() {
        String json = "{\"id\":1,\"name\":\"Item Name\",\"description\":\"Item Description\",\"available\":true,\"ownerId\":100,\"requestId\":200,\"nextBooking\":{},\"lastBooking\":{},\"comments\":[{}]}";

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        assertEquals(1L, itemDto.getId());
        assertEquals("Item Name", itemDto.getName());
        assertEquals("Item Description", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
        assertEquals(100L, itemDto.getOwnerId());
        assertEquals(200L, itemDto.getRequestId());
        assertNotNull(itemDto.getNextBooking());
        assertNotNull(itemDto.getLastBooking());
        assertEquals(1, itemDto.getComments().size());
    }
}

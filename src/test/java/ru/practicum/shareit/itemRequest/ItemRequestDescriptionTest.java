package ru.practicum.shareit.itemRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.request.dto.ItemDescriptionRequestDto;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemRequestDescriptionTest {

    @Autowired
    private ObjectMapper objectMapper;

    public ItemRequestDescriptionTest() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
    }

    @Test
    @SneakyThrows
    public void testSerialize() {
        ItemDescriptionRequestDto itemRequestDescription = new ItemDescriptionRequestDto();
        itemRequestDescription.setDescription("Item description");

        String json = objectMapper.writeValueAsString(itemRequestDescription);

        String expectedJson = "{\"description\":\"Item description\"}";
        assertEquals(expectedJson, json);
    }

    @Test
    @SneakyThrows
    public void testDeserialize() {
        String json = "{\"description\":\"Item description\"}";

        ItemDescriptionRequestDto itemRequestDescription = objectMapper.readValue(json, ItemDescriptionRequestDto.class);

        assertEquals("Item description", itemRequestDescription.getDescription());
    }
}

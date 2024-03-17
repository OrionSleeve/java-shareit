package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemDescriptionRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.format.DateTimeFormatter;

public class ItemRequestMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static ItemRequest fromItemRequestDto(ItemDescriptionRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }

    public static ItemRequestDto toRequestWithItemsDto(ItemRequest itemRequest) {
        String created = DATE_TIME_FORMATTER.format(itemRequest.getCreated());

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(created)
                .build();
    }
}

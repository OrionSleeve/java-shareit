package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForRequestDto;

import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    private long id;
    private String description;
    private String created;
    private List<ItemForRequestDto> items;
}

package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemForRequestDto {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private long requestId;

    public ItemForRequestDto(Long id, String name, String description, boolean available, long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}

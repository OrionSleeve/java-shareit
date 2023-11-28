package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Status available;
    private User owner;
    private ItemRequest request;
}

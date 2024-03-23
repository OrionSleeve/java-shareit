package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@Builder
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;
    private BookingDto nextBooking;
    private BookingDto lastBooking;
    private List<CommentResDto> comments;
}

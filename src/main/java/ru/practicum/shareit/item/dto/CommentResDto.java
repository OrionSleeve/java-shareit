package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentResDto {
    private long id;
    private String text;
    private String authorName;
    private String created;
}
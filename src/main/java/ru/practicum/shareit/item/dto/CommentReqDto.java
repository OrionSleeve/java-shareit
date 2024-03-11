package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommentReqDto {
    @NotBlank
    @Size(max = 512)
    private String text;
}

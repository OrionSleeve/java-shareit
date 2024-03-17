package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ItemDescriptionRequestDto {
    @Size(max = 512)
    @NotEmpty
    @NotNull
    private String description;
}

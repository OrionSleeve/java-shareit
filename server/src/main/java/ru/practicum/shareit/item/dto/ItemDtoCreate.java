package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.GroupsInterface;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDtoCreate {
    @NotBlank(groups = {GroupsInterface.Create.class})
    @Size(max = 255, groups = {GroupsInterface.Create.class, GroupsInterface.Update.class})
    private String name;

    @NotBlank(groups = {GroupsInterface.Create.class})
    @Size(max = 512, groups = {GroupsInterface.Create.class, GroupsInterface.Update.class})
    private String description;

    @NotNull(groups = {GroupsInterface.Create.class})
    private Boolean available;
    private Long requestId;
}

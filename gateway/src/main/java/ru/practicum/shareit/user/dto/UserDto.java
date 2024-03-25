package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.GroupsInterface;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserDto {
    private long id;
    @Size(groups = {GroupsInterface.Create.class, GroupsInterface.Update.class}, max = 255)
    @NotBlank(groups = GroupsInterface.Create.class)
    private String name;
    @Size(groups = {GroupsInterface.Create.class, GroupsInterface.Update.class}, max = 512)
    @NotBlank(groups = GroupsInterface.Create.class)
    @Email(groups = {GroupsInterface.Create.class, GroupsInterface.Update.class})
    private String email;
}

package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.GroupsInterface;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class UserDto {
    private long id;
    @NotBlank(groups = {GroupsInterface.Create.class})
    private String name;
    @NotEmpty(groups = {GroupsInterface.Create.class})
    @Email(groups = {GroupsInterface.Create.class, GroupsInterface.Update.class})
    private String email;
}

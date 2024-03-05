package ru.practicum.shareit.user.userService;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addNewUser(UserDto userDto);

    void removeUser(long userId);

    UserDto getUserById(long userId);

    List<UserDto> getAllUsers();

    UserDto updateUserData(long userId, UserDto userDto);
}

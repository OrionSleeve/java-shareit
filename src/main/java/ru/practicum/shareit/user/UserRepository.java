package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    UserDto addNewUser(UserDto userDto);

    void removeUser(long userId);

    UserDto getUserById(long userId);

    List<UserDto> getAllUsers();

    UserDto updateUserData(long userId, UserDto userDto);
}

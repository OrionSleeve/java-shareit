package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    UserDto createUser(UserDto userDto);

    void removeUser(long userId);

    UserDto getUserById(long userId);

    List<UserDto> getAllUsers();

    UserDto userUpdate(long userId, UserDto userDto);
}

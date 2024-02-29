package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserDto addNewUser(UserDto userDto);

    void removeUser(long userId);

    UserDto getUserById(long userId);

    List<UserDto> getAllUsers();

    UserDto updateUserData(long userId, UserDto userDto);
}

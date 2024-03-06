package ru.practicum.shareit.user.userStorage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User addNewUser(User user);

    void removeUser(long userId);

    User getUserById(long userId);

    List<User> getAllUsers();

    User updateUserData(long userId, User user);
}

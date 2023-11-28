package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    User createUser(User user);

    void removeUser(long userId);

    User getUserById(long userId);

    List<User> getAllUsers();

    User userUpdate(long userId, User user);
}

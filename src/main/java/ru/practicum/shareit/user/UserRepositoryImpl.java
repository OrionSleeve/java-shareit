package ru.practicum.shareit.user;

import java.util.*;

public class UserRepositoryImpl implements UserRepository {
    private long userId = 0L;
    private Set<String> emailContainer = new HashSet<>();
    private Map<Long, User> userMap = new HashMap<>();
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(userId++);
        userMap.put(user.getId(), user);
        emailContainer.add(user.getEmail());
        return UserMapper.toUserDto(user);
    }

    @Override
    public void removeUser(long userId) {
    }

    @Override
    public UserDto getUserById(long userId) {
        return null;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return null;
    }

    @Override
    public UserDto userUpdate(long userId, UserDto userDto) {
        return null;
    }
}

package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserRepositoryImpl implements UserRepository {
    private long userId = 0L;
    private final Set<String> emailData = new HashSet<>();
    private final Map<Long, User> userData = new HashMap<>();

    @Override
    public UserDto addNewUser(UserDto userDto) {
        validateEmail(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        user.setId(generatedId());
        userData.put(user.getId(), user);
        emailData.add(user.getEmail());
        return UserMapper.toUserDto(user);
    }

    @Override
    public void removeUser(long userId) {
        getUserById(userId);
        String email = userData.get(userId).getEmail();
        userData.remove(userId);
        emailData.remove(email);
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = userData.get(userId);
        if (user != null) {
            return UserMapper.toUserDto(user);
        } else {
            throw new NotFoundException("User not found with userId: " + userId);
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        return new ArrayList<>(userData.values()).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUserData(long userId, UserDto userDto) {
        User userUpdate = UserMapper.toUser(getUserById(userId));
        updateFields(userUpdate, userDto);
        userData.put(userUpdate.getId(), userUpdate);
        return getUserById(userId);
    }

    private void updateFields(User user, UserDto userDto) {
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            if (!user.getEmail().equals(userDto.getEmail())) {
                validateEmail(userDto.getEmail());
            }
            emailData.remove(user.getEmail());
            user.setEmail(userDto.getEmail());
            emailData.add(user.getEmail());
        }
    }

    private void validateEmail(String email) {
        if (emailData.contains(email)) {
            throw new ConflictException("Email is already registered" + email);
        }
    }

    private long generatedId() {
        return ++userId;
    }
}

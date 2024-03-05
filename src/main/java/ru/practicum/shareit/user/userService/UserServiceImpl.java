package ru.practicum.shareit.user.userService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.userRepository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addNewUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User savedUser = userRepository.addNewUser(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUserData(long userId, UserDto userDto) {
        User newUser = UserMapper.toUser(userDto);
        User updatedUser = userRepository.updateUserData(userId, newUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = userRepository.getUserById(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void removeUser(long userId) {
        userRepository.removeUser(userId);
    }

}

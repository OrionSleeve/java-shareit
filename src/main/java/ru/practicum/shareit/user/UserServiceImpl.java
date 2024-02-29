package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addNewUser(UserDto userDto) {
        return userRepository.addNewUser(userDto);
    }

    @Override
    public UserDto updateUserData(long userId, UserDto userDto) {
        return userRepository.updateUserData(userId, userDto);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public UserDto getUserById(long userId) {
        return userRepository.getUserById(userId);
    }

    @Override
    public void removeUser(long userId) {
        userRepository.removeUser(userId);
    }
}

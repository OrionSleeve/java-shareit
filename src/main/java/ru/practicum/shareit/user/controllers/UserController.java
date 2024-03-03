package ru.practicum.shareit.user.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.GroupsInterface;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.userService.UserService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody @Validated(GroupsInterface.Create.class) UserDto userDto) {
        log.info("Received request to create user: {}", userDto);
        return userService.addNewUser(userDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Received request to retrieve all users");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("Received request to retrieve user with ID: {}", userId);
        return userService.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId,
                              @RequestBody @Validated(GroupsInterface.Update.class) UserDto userDto) {
        log.info("Received request to update user with ID: {} with data: {}", userId, userDto);
        return userService.updateUserData(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable long userId) {
        log.info("Received request to remove user with ID: {}", userId);
        userService.removeUser(userId);
    }
}

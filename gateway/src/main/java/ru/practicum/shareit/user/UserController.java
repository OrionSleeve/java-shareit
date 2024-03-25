package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.GroupsInterface;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Validated(GroupsInterface.Create.class) UserDto userDto) {
        log.info("Received request to create user: {}", userDto);
        return userClient.addNewUser(userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Received request to retrieve all users");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("Received request to retrieve user with ID: {}", userId);
        return userClient.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                             @RequestBody @Validated(GroupsInterface.Update.class) UserDto userDto) {
        log.info("Received request to update user with ID: {} with data: {}", userId, userDto);
        return userClient.updateUserData(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> removeUser(@PathVariable long userId) {
        log.info("Received request to remove user with ID: {}", userId);
        return userClient.removeUser(userId);
    }
}

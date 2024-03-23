package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@DataJpaTest
public class UserRepositoryJpaTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    public void createUser_WhenValid_ThenReturnUser() {
        UserDto userDto = UserDto.builder().name("John").email("john@example.com").build();
        User user = UserMapper.toUser(userDto);
        User saveResponse = userRepository.save(user);
        user.setId(saveResponse.getId());
        Long userId = user.getId();
        Optional<User> getResponse = userRepository.findById(userId);

        assertEquals(user, saveResponse);
        assertEquals(user, getResponse.get());
    }

    @Test
    public void getAllUsers_WhenValid_ThenReturnUsersList() {
        UserDto userDto1 = UserDto.builder().name("John").email("john@example.com").build();
        UserDto userDto2 = UserDto.builder().name("Jane").email("jane@example.com").build();
        User user1 = UserMapper.toUser(userDto1);
        User user2 = UserMapper.toUser(userDto2);
        user1.setId(userRepository.save(user1).getId());
        user2.setId(userRepository.save(user2).getId());

        List<UserDto> expectedListOfUsers = new ArrayList<>();
        expectedListOfUsers.add(UserMapper.toUserDto(user1));
        expectedListOfUsers.add(UserMapper.toUserDto(user2));

        List<UserDto> actualListOfUsers = userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

        assertEquals(expectedListOfUsers, actualListOfUsers);
    }

    @Test
    public void updateUser_WhenValid_ThenReturnUpdatedUser() {
        UserDto userDto = UserDto.builder().name("John").email("john@example.com").build();
        User user = UserMapper.toUser(userDto);
        user.setId(userRepository.save(user).getId());

        UserDto update = UserDto.builder().email("newemail@example.com").build();
        user.setEmail(update.getEmail());

        Long userId = user.getId();
        userRepository.updateUserFields(UserMapper.toUser(update), userId);
        em.flush();
        em.clear();
        Optional<User> updatedUser = userRepository.findById(userId);

        assertEquals(user, updatedUser.get());
        userRepository.delete(updatedUser.get());
        assertFalse(userRepository.findById(userId).isPresent());
    }

    @AfterEach
    public void delete() {
        userRepository.deleteAll();
    }
}

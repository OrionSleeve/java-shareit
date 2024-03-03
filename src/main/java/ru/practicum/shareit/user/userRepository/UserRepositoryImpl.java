package ru.practicum.shareit.user.userRepository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private long userId = 0L;
    private final Set<String> emailData = new HashSet<>();
    private final Map<Long, User> userData = new HashMap<>();

    @Override
    public User addNewUser(User user) {
        validateEmail(user.getEmail());
        user.setId(generatedId());
        userData.put(user.getId(), user);
        emailData.add(user.getEmail());
        return user;
    }

    @Override
    public void removeUser(long userId) {
        getUserById(userId);
        String email = userData.get(userId).getEmail();
        userData.remove(userId);
        emailData.remove(email);
    }

    @Override
    public User getUserById(long userId) {
        User user = userData.get(userId);
        if (user != null) {
            return user;
        } else {
            throw new NotFoundException("User not found with userId: " + userId);
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userData.values());
    }

    @Override
    public User updateUserData(long userId, User user) {
        User existingUser = getUserById(userId);
        updateFields(existingUser, user);
        return existingUser;
    }

    private void updateFields(User existingUser, User newUser) {
        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            existingUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            if (!existingUser.getEmail().equals(newUser.getEmail())) {
                validateEmail(newUser.getEmail());
            }
            emailData.remove(existingUser.getEmail());
            existingUser.setEmail(newUser.getEmail());
            emailData.add(existingUser.getEmail());
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

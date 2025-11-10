package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository implements UserStorage {
    private final HashMap<Integer, User> userRepository = new HashMap<>();
    private Integer idCount = 0;

    public Optional<User> getUserOnId(int id) {
        return Optional.ofNullable(userRepository.get(id));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.values());
    }

    public User addUser(User user) {
        user.setId(idCounter());
        userRepository.put(user.getId(), user);
        return user;

    }

    public User updateUser(User user, int userId) {
        User updateUser = userRepository.get(userId);
        if (user.getName() != null && !user.getName().isBlank() && user.getEmail() != null &&
                !user.getEmail().isBlank()) {
            updateUser.setName(user.getName());
            updateUser.setEmail(user.getEmail());
        }
        if ((user.getName() == null || user.getName().isBlank()) && user.getEmail() != null &&
                !(user.getEmail().isBlank())) {
            updateUser.setEmail(user.getEmail());
        }
        if (user.getName() != null && !(user.getName().isBlank()) &&
                (user.getEmail() == null || user.getEmail().isBlank())) {
            updateUser.setName(user.getName());
        }
        return updateUser;
    }

    public void deleteUser(int userId) {
        userRepository.remove(userId);
    }

    private Integer idCounter() {
        idCount++;
        return idCount;
    }
}

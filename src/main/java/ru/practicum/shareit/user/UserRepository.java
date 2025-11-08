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
        User user = userRepository.get(id);
        if (user != null) {
            return Optional.of(user);
        }
        return Optional.empty();
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
        User user1 = userRepository.get(userId);
        if (user.getName() != null && !user.getName().isBlank() && user.getEmail() != null &&
                !user.getEmail().isBlank()) {
            user1.setName(user.getName());
            user1.setEmail(user.getEmail());
        }
        if ((user.getName() == null || user.getName().isBlank()) && user.getEmail() != null &&
                !(user.getEmail().isBlank())) {
            user1.setEmail(user.getEmail());
        }
        if (user.getName() != null && !(user.getName().isBlank()) &&
                (user.getEmail() == null || user.getEmail().isBlank())) {
            user1.setName(user.getName());
        }
        return user1;
    }

    public void deleteUser(int userId) {
        userRepository.remove(userId);
    }

    private Integer idCounter() {
        idCount++;
        return idCount;
    }
}

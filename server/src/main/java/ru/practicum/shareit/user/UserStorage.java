package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> getUserOnId(int id);

    User addUser(User user);

    User updateUser(User user, int userId);

    void deleteUser(int userId);

    List<User> getAllUsers();
}

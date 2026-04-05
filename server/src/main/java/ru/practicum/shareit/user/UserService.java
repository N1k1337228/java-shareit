package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.model.SameEmailException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userStorage;
    private final UserMapper userMapper;

    public UserDto getUser(int userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        log.info("");
        return userMapper.toUserDto(user);
    }

    public UserDto addUser(UserDto userDto) {
        if (isSameEmail(userDto)) {
            log.error("Попытка добавить пользователя с уже существующим email");
            throw new SameEmailException("Уже есть пользователь с таким адресом электронной почты");
        }
        User newUser = userStorage.save(userMapper.toUser(userDto));
        log.info("Пользователь с id {} был добавлен", newUser.getId());
        return userMapper.toUserDto(newUser);
    }

    public UserDto updateUser(UserDto userDto, int userId) {
        if (isSameEmail(userDto)) {
            log.error("Попытка добавить пользователя с уже существующим email");
            throw new SameEmailException("Уже есть пользователь с таким адресом электронной почты");
        }
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден"));
        if (!user.getId().equals(userId)) {
            log.error("Попытка обновления данных одного пользователя другим пользователем с id {}", userId);
            throw new ValidationException("Обновлять данные пользователя может только сам пользователь");
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        User updatedUser = userStorage.save(user);
        log.info("Пользователь с id {} был обновлён", userId);
        return userMapper.toUserDto(updatedUser);
    }

    public void deleteUser(int userId) {
        if (userStorage.findById(userId).isPresent()) {
            userStorage.deleteById(userId);
            log.info("Пользователь с id {} был удалён", userId);
            return;
        }
        log.error("Попытка удалить несуществующего пользователя");
        throw new NotFoundException("Пользователь с id " + userId + " не найден");
    }

    private boolean isSameEmail(UserDto userDto) {
        List<User> users = userStorage.findAll().stream()
                .filter(user -> user.getEmail().equals(userDto.getEmail()))
                .collect(Collectors.toList());
        return !users.isEmpty();
    }
}
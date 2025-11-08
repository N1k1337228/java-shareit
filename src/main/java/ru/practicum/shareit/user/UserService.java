package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.model.SameEmailException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public UserDto getUser(int userId) {
        User user = userStorage.getUserOnId(userId)
                .orElseThrow(() -> new NotFoundException("Запрашиваемый пользователь не найден"));
        log.info("");
        return userMapper.toUserDto(user);
    }

    public UserDto addUser(UserDto userDto) {
        List<User> users = userStorage.getAllUsers().stream()
                .filter(user -> user.getEmail().equals(userDto.getEmail()))
                .collect(Collectors.toList());
        if (!users.isEmpty()) {
            log.error("Попытка добавить пользователя с уже существующим email");
            throw new SameEmailException("Уже есть пользователь с таким адресом электронной почты");
        }
        userStorage.addUser(userMapper.toUser(userDto));
        log.info("");
        return userDto;
    }

    public UserDto updateUser(UserDto userDto, int userId) {
        if (userStorage.getUserOnId(userId).isPresent()) {
            User user = userStorage.updateUser(userMapper.toUser(userDto), userId);
            log.info("Пользователь был обновлён");
        }
        log.error("Попытка обновить несуществующего пользователя");
        throw new NotFoundException("Пользователь не был найден");
    }

    public void deleteUser(int userId) {
        if (userStorage.getUserOnId(userId).isPresent()) {
            userStorage.deleteUser(userId);
            log.info("Пользователь был удалён");
        }
        log.error("Попытка удалить несуществующего пользователя");
        throw new NotFoundException("Пользователь не был найден");
    }
}

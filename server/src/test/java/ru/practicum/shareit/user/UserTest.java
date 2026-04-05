package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.model.SameEmailException;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserTest {
    @Mock
    UserRepository userStorage;  // мок репозитория
    @Mock
    UserMapper userMapper;    // мок маппера
    @InjectMocks
    UserService userService;
    UserDto inputDto;
    User userEntity;
    User savedUser;
    UserDto expectedDto;

    @BeforeEach
    void createTestData() {
        inputDto = new UserDto();
        inputDto.setName("Test");
        inputDto.setEmail("test@mail.com");
        userEntity = new User();
        userEntity.setName("Test");
        userEntity.setEmail("test@mail.com");
        savedUser = new User();
        savedUser.setId(1);
        savedUser.setName("Test");
        savedUser.setEmail("test@mail.com");
        expectedDto = new UserDto();
        expectedDto.setId(1);
        expectedDto.setName("Test");
        expectedDto.setEmail("test@mail.com");
    }

    @Test
    void addUser_Success() {
        User user1 = new User();
        user1.setId(2);
        user1.setEmail("test123@mail.com");
        user1.setName("123");
        Mockito.when(userStorage.findAll()).thenReturn(List.of(user1));
        Mockito.when(userMapper.toUser(inputDto)).thenReturn(userEntity);
        Mockito.when(userStorage.save(userEntity)).thenReturn(savedUser);
        Mockito.when(userMapper.toUserDto(savedUser)).thenReturn(expectedDto);
        UserDto result = userService.addUser(inputDto);
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("test@mail.com", result.getEmail());
    }

    @Test
    void sameEmailAddUserTest() {
        User user = new User();
        user.setId(2);
        user.setEmail("test@mail.com");
        user.setName("1234");
        Mockito.when(userStorage.findAll()).thenReturn(List.of(user));
        Assertions.assertThrows(SameEmailException.class, () -> userService.addUser(inputDto));
    }

    @Test
    void getUserTest() {
        Mockito.when(userStorage.findById(Mockito.anyInt())).thenReturn(Optional.of(savedUser));
        Mockito.when(userMapper.toUserDto(savedUser)).thenReturn(expectedDto);
        UserDto result = userService.getUser(1);
        Assertions.assertEquals(1, result.getId());
    }

    @Test
    void patchUserTest() {
        User user1 = new User();
        user1.setId(2);
        user1.setEmail("test123@mail.com");
        user1.setName("123");
        UserDto user2 = new UserDto();
        user2.setEmail("test222@mail.com");
        user2.setName("222");
        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setName("222");
        updatedUser.setEmail("test@mail.com");
        UserDto expectedUser2 = new UserDto();
        expectedUser2.setId(1);
        expectedUser2.setEmail("test222@mail.com");
        expectedUser2.setName("222");
        Mockito.when(userStorage.findAll()).thenReturn(List.of(user1));
        Mockito.when(userStorage.findById(Mockito.anyInt())).thenReturn(Optional.of(savedUser));
        Mockito.when(userStorage.save(savedUser)).thenReturn(updatedUser);
        Mockito.when(userMapper.toUserDto(updatedUser)).thenReturn(expectedUser2);
        UserDto result = userService.updateUser(user2, 1);
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("test222@mail.com", result.getEmail());
    }

    @Test
    void deleteUserTest() {
        Mockito.when(userStorage.findById(1)).thenReturn(Optional.of(savedUser));
        userService.deleteUser(1);
        Mockito.verify(userStorage, Mockito.times(1))
                .deleteById(1);
    }

    @Test
    void deleteNotFoundUserTest() {
        Mockito.when(userStorage.findById(1)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.deleteUser(1));
        NotFoundException exception =
                Assertions.assertThrows(NotFoundException.class, () -> userService.deleteUser(1));
        Assertions.assertEquals("Пользователь с id 1 не найден", exception.getMessage());
    }
}
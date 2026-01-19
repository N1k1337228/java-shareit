package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = {
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=always"
},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRepositoryTest {

    private final UserService userService;

    @Test
    void getUserTest() {
        UserDto createDto = new UserDto();
        createDto.setName("Test User");
        createDto.setEmail("test@mail.com");
        UserDto createdUser = userService.addUser(createDto);
        UserDto foundUser = userService.getUser(createdUser.getId());
        assertNotNull(foundUser);
        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals("Test User", foundUser.getName());
        assertEquals("test@mail.com", foundUser.getEmail());
    }

    @Test
    void addUserTest() {
        UserDto inputDto = new UserDto();
        inputDto.setName("New User");
        inputDto.setEmail("new@mail.com");
        UserDto createdUser = userService.addUser(inputDto);
        assertNotNull(createdUser.getId());
        assertEquals("New User", createdUser.getName());
        assertEquals("new@mail.com", createdUser.getEmail());
    }

    @Test
    void updateUserTest() {
        UserDto createDto = new UserDto();
        createDto.setName("Original");
        createDto.setEmail("original@mail.com");
        UserDto createdUser = userService.addUser(createDto);
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated");
        updateDto.setEmail("updated@mail.com");
        UserDto updatedUser = userService.updateUser(updateDto, createdUser.getId());
        assertEquals(createdUser.getId(), updatedUser.getId());
        assertEquals("Updated", updatedUser.getName());
        assertEquals("updated@mail.com", updatedUser.getEmail());
    }

    @Test
    void deleteUserTest() {
        UserDto createDto = new UserDto();
        createDto.setName("To Delete");
        createDto.setEmail("delete@mail.com");
        UserDto createdUser = userService.addUser(createDto);
        userService.deleteUser(createdUser.getId());
        assertThrows(NotFoundException.class, () -> {
            userService.getUser(createdUser.getId());
        });
    }
}
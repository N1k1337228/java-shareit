package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.model.SameEmailException;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Test
    void saveNewUserTest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(2);
        userDto.setEmail("test@mail.com");
        userDto.setName("1234");
        when(userService.addUser(any()))
                .thenReturn(userDto);
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void updateUserTest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setName("Test");
        userDto.setEmail("test@mail.com");
        when(userService.updateUser(any(UserDto.class), eq(1)))
                .thenReturn(userDto);
        mockMvc.perform(patch("/users/{userid}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    void getUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(2);
        userDto.setEmail("test@mail.com");
        userDto.setName("1234");
        when(userService.getUser(eq(1)))
                .thenReturn(userDto);
        mockMvc.perform(get("/users/{userid}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void deleteUserTest() throws Exception {
        int userId = 1;
        doNothing().when(userService).deleteUser(userId);
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());
    }

    @Test
    void getUserNotFoundTest() throws Exception {
        when(userService.getUser(999))
                .thenThrow(new NotFoundException("Пользователь не найден"));
        mockMvc.perform(get("/users/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void addUserSameEmailTest() throws Exception {
        UserDto inputDto = new UserDto();
        inputDto.setName("Test");
        inputDto.setEmail("duplicate@mail.com");
        when(userService.addUser(any(UserDto.class)))
                .thenThrow(new SameEmailException("Email уже существует"));
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(inputDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void updateUserNotFoundTest() throws Exception {
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated");

        when(userService.updateUser(any(UserDto.class), eq(999)))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(patch("/users/{id}", 999)
                        .content(mapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserWhenNotFound() throws Exception {
        doThrow(new NotFoundException("Пользователь не найден"))
                .when(userService).deleteUser(999);
        mockMvc.perform(delete("/users/{userId}", 999))
                .andExpect(status().isNotFound());
    }
}
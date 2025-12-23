package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * TODO Sprint add-controllers.
 */
@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") @Positive @NotNull Integer id) {
        return userService.getUser(id);
    }

    @PostMapping
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        return userService.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto,
                              @PathVariable("userId") @NotNull Integer id) {
        return userService.updateUser(userDto, id);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") @Positive @NotNull Integer id) {
        userService.deleteUser(id);
    }
}

package ru.practicum.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") @Positive @NotNull
                                                Integer userId, @RequestBody @Valid RequestDto requestDto) {
        return requestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsersResponses(@RequestHeader("X-Sharer-User-Id")
                                                       @Positive @NotNull Integer userId) {
        return requestClient.getAllUsersResponses(userId);

    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllResponses(@RequestHeader("X-Sharer-User-Id")
                                                  @Positive @NotNull Integer userId) {
        return requestClient.getAllResponses(userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequestOnId(@PathVariable @Positive @NotNull Integer requestId,
                                                 @RequestHeader("X-Sharer-User-Id") @Positive @NotNull Integer userId) {
        return requestClient.getRequestOnId(requestId, userId);
    }
}

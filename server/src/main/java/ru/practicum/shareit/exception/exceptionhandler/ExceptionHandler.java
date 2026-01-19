package ru.practicum.shareit.exception.exceptionhandler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.model.ErrorResponse;
import ru.practicum.shareit.exception.model.SameEmailException;

@RestControllerAdvice
public class ExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ErrorResponse notFoundHandler(NotFoundException e) {
        return new ErrorResponse("notFoundError", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ErrorResponse validationExceptionHandler(ValidationException e) {
        return new ErrorResponse("validationException", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ErrorResponse constraintViolationExceptionHandler(ConstraintViolationException e) {
        return new ErrorResponse("constraintViolationException", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ErrorResponse sameEmailHandler(SameEmailException e) {
        return new ErrorResponse("sameEmail", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ErrorResponse internalServerErrorHandler(RuntimeException e) {
        return new ErrorResponse("возникла ошибка на сервере", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ErrorResponse constraintViolationExceptionHandler(MethodArgumentNotValidException e) {
        return new ErrorResponse("methodArgumentNotValidException", e.getMessage());
    }

}

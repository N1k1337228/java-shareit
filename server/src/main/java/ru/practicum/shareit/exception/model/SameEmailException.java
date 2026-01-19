package ru.practicum.shareit.exception.model;

public class SameEmailException extends RuntimeException {
  public SameEmailException(String message) {
    super(message);
  }
}

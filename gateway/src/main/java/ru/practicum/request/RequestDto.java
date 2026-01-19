package ru.practicum.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class RequestDto {
    @NotBlank
    private String description;

}

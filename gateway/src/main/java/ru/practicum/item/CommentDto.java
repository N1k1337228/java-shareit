package ru.practicum.item;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Integer id;
    private Integer ownerOfCommentId;
    private String authorName;
    private Integer itemId;
    @NotBlank
    private String text;
    private LocalDateTime created;
}

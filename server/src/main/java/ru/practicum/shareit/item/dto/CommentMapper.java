package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public class CommentMapper {

    public static Comment toComment(CommentDto commentDto, User author, Item item) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setOwnerOfComment(author);
        comment.setCreated(commentDto.getCreated());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setAuthorName(comment.getOwnerOfComment().getName());
        commentDto.setText(comment.getText());
        commentDto.setOwnerOfCommentId(comment.getOwnerOfComment().getId());
        commentDto.setItemId(comment.getItem().getId());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }
}
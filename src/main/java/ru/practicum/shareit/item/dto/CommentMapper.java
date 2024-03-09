package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static Comment fromCommentRequestDto(CommentReqDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        return comment;
    }

    public static CommentResDto toCommentResponseDto(Comment comment) {
        String created = DATE_TIME_FORMATTER.format(comment.getCreated());

        return CommentResDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthorName().getName())
                .created(created)
                .build();
    }

    public static List<CommentResDto> toCommentListResponseDto(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
    }
}

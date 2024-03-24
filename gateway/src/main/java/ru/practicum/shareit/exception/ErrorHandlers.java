package ru.practicum.shareit.exception;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandlers {

    @ExceptionHandler({ConstraintViolationException.class, HttpStatusCodeException.class,
            InvalidRequestException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerBadRequest(final RuntimeException exception) {
        log.debug("Получен статус 404 Bad request {}", exception.getMessage(), exception);
        return ErrorResponse.builder().error(exception.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerInternalServerError(final Throwable exception) {
        log.debug("Получен статус 500 Internal server error {}", exception.getMessage(), exception);
        return ErrorResponse.builder().error(exception.getMessage()).build();
    }

    @Data
    @Builder
    public static class ErrorResponse {
        private final String error;
    }
}

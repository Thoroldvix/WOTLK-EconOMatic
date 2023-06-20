package com.thoroldvix.pricepal.error;


import com.thoroldvix.pricepal.item.ItemAlreadyExistsException;
import com.thoroldvix.pricepal.item.ItemDoesNotExistException;
import com.thoroldvix.pricepal.shared.NumberNotPositiveException;
import feign.FeignException;
import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@RestControllerAdvice
class ControllerAdvice {

    public static final String NOT_FOUND = "Not found";
    public static final String BAD_REQUEST = "Bad request";

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<ApiError> handleNotFoundException(NotFoundException e, HttpServletRequest request) {
        ApiError apiError = getApiError(e.getLocalizedMessage(), HttpStatus.NOT_FOUND, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(FeignException.NotFound.class)
    protected ResponseEntity<ApiError> handleFeignNotFoundException(FeignException.NotFound e, HttpServletRequest request) {
        ApiError apiError = getApiError(NOT_FOUND, HttpStatus.NOT_FOUND, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(NoResultException.class)
    protected ResponseEntity<ApiError> handleNoResultException(NoResultException e, HttpServletRequest request) {
        ApiError apiError = getApiError(NOT_FOUND, HttpStatus.NOT_FOUND, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        ApiError apiError = getApiError(BAD_REQUEST, HttpStatus.BAD_REQUEST, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        ApiError apiError = getApiError(BAD_REQUEST, HttpStatus.BAD_REQUEST, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(DateTimeParseException.class)
    protected ResponseEntity<ApiError> handleDateTimeParseException(DateTimeParseException e, HttpServletRequest request) {
        ApiError apiError = getApiError("Invalid date format provided", HttpStatus.BAD_REQUEST, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(ItemAlreadyExistsException.class)
    protected ResponseEntity<ApiError> handleItemAlreadyExistsException(ItemAlreadyExistsException e, HttpServletRequest request) {
        ApiError apiError = getApiError("Item with the same id or serverName as the one you are trying to add already exists", HttpStatus.BAD_REQUEST, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(ItemDoesNotExistException.class)
    protected ResponseEntity<ApiError> handleItemDoesNotExistException(ItemDoesNotExistException e, HttpServletRequest request) {
        ApiError apiError = getApiError("Item with the provided identifier does not exist", HttpStatus.BAD_REQUEST, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(NumberNotPositiveException.class)
    protected ResponseEntity<ApiError> handleNumberNotPositiveException(NumberNotPositiveException e, HttpServletRequest request) {
        ApiError apiError = getApiError(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    private ApiError getApiError(String errorMessage, HttpStatus status, HttpServletRequest request) {
        return ApiError.builder()
                .status(status.value())
                .error(errorMessage)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
    }
}
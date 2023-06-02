package com.thoroldvix.pricepal.common;

import com.thoroldvix.pricepal.server.error.G2GPriceNotFoundException;
import com.thoroldvix.pricepal.server.error.ServerNotFoundException;
import com.thoroldvix.pricepal.server.error.ServerPriceNotFoundException;
import com.vaadin.flow.router.NotFoundException;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException e, HttpServletRequest request) {
        ApiError apiError = getApiError(e, HttpStatus.NOT_FOUND, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ApiError> handleFeignNotFoundException(FeignException.NotFound e, HttpServletRequest  request) {
        ApiError apiError = getApiError(e, HttpStatus.NOT_FOUND, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad request")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(G2GPriceNotFoundException.class)
    public ResponseEntity<ApiError> handleG2GPriceNotFoundException(G2GPriceNotFoundException e, HttpServletRequest request) {
        ApiError apiError = getApiError(e, HttpStatus.NOT_FOUND, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(ServerPriceNotFoundException.class)
    public ResponseEntity<ApiError> handleServerPriceNotFoundException(ServerPriceNotFoundException e, HttpServletRequest request) {
          ApiError apiError = getApiError(e, HttpStatus.NOT_FOUND, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(ServerNotFoundException.class)
    public ResponseEntity<ApiError> handleServerNotFoundException(ServerNotFoundException e,  HttpServletRequest request) {
        ApiError apiError = getApiError(e, HttpStatus.NOT_FOUND, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }


    private ApiError getApiError(RuntimeException e, HttpStatus status, HttpServletRequest request) {
        return ApiError.builder()
                .status(status.value())
                .error(e.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
    }
}
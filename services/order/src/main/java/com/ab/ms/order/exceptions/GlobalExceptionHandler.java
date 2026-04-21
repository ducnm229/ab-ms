package com.ab.ms.order.exceptions;

import com.ab.ms.order.dto.ErrorResponseDto;
import com.ab.ms.order.dto.InvalidDataErrorDto;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Map<String, String> DISPLAY_FIELD_NAMES = Map.of(
            "orderId", "Order ID"
    );

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @Nullable HttpHeaders headers,
            @Nullable HttpStatusCode status,
            @Nullable WebRequest request
    ) {
        Map<String, String> validationErrors = new HashMap<>();
        List<ObjectError> errorList = ex.getBindingResult().getAllErrors();

        errorList.forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new InvalidDataErrorDto(
                HttpStatus.BAD_REQUEST.toString(),
                validationErrors,
                LocalDateTime.now()
        ));
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            @NonNull TypeMismatchException ex,
            @Nullable HttpHeaders headers,
            @Nullable HttpStatusCode status,
            @Nullable WebRequest request
    ) {
        String friendlyName = DISPLAY_FIELD_NAMES.getOrDefault(ex.getPropertyName(), ex.getPropertyName());
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

        ErrorResponseDto body = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.toString(),
                String.format("%s must be of type %s", friendlyName, requiredType),
                LocalDateTime.now(),
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );

        return handleExceptionInternal(ex, body, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception exception, WebRequest webRequest) {
        return new ResponseEntity<>(
                createErrorResponseDto(exception, webRequest, HttpStatus.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(
            ResourceNotFoundException exception,
            WebRequest webRequest
    ) {
        return new ResponseEntity<>(
                createErrorResponseDto(exception, webRequest, HttpStatus.NOT_FOUND),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(InvalidOrderSagaTransitionException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidOrderSagaTransitionException(
            InvalidOrderSagaTransitionException exception,
            WebRequest webRequest
    ) {
        return new ResponseEntity<>(
                createErrorResponseDto(exception, webRequest, HttpStatus.CONFLICT),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(NonRetryableSagaException.class)
    public ResponseEntity<ErrorResponseDto> handleNonRetryableSagaException(
            NonRetryableSagaException exception,
            WebRequest webRequest
    ) {
        return new ResponseEntity<>(
                createErrorResponseDto(exception, webRequest, HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UnknownProductException.class)
    public ResponseEntity<ErrorResponseDto> handleUnknownProductException(
            UnknownProductException exception,
            WebRequest webRequest
    ) {
        return new ResponseEntity<>(
                createErrorResponseDto(exception, webRequest, HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidCustomerException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidCustomerException(
            InvalidCustomerException exception,
            WebRequest webRequest
    ) {
        return new ResponseEntity<>(
                createErrorResponseDto(exception, webRequest, HttpStatus.UNPROCESSABLE_ENTITY),
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ErrorResponseDto> handleCallNotPermittedException(
            CallNotPermittedException exception,
            WebRequest webRequest
    ) {
        return new ResponseEntity<>(
                createErrorResponseDto(exception, webRequest, HttpStatus.SERVICE_UNAVAILABLE),
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    private ErrorResponseDto createErrorResponseDto(Exception exception, WebRequest webRequest, HttpStatus httpStatus) {
        return new ErrorResponseDto(
                httpStatus.toString(),
                exception.getMessage(),
                LocalDateTime.now(),
                webRequest.getDescription(false)
        );
    }
}

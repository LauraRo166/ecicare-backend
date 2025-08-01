package edu.escuelaing.ecicare.exceptions;

import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import edu.escuelaing.ecicare.models.dto.ApiErrorDto;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiErrorDto> handleResourceNotFoundException(ResourceNotFoundException exception,
                        HttpServletRequest request) {
                ApiErrorDto apiError = ApiErrorDto.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                                .message(exception.getMessage())
                                .path(request.getServletPath())
                                .build();
                return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(IOException.class)
        public ResponseEntity<ApiErrorDto> handleIOException(IOException exception, HttpServletRequest request) {
                ApiErrorDto apiError = ApiErrorDto.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                                .message(exception.getMessage())
                                .path(request.getServletPath())
                                .build();
                return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(RedeemAwardException.class)
        public ResponseEntity<ApiErrorDto> handleRedeemAwardException(RedeemAwardException exception,
                        HttpServletRequest request) {
                ApiErrorDto apiError = ApiErrorDto.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message(exception.getMessage())
                                .path(request.getServletPath())
                                .build();
                return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MedicalApproveException.class)
        public ResponseEntity<ApiErrorDto> handleMedicalApproveException(MedicalApproveException exception,
                        HttpServletRequest request) {
                ApiErrorDto apiError = ApiErrorDto.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.FORBIDDEN.value())
                                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                                .message(exception.getMessage())
                                .path(request.getServletPath())
                                .build();
                return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiErrorDto> handlePropertyValueException(Exception exception,
                        HttpServletRequest request) {
                ApiErrorDto apiError = ApiErrorDto.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                                .message(exception.getMessage())
                                .path(request.getServletPath())
                                .build();
                return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }

}

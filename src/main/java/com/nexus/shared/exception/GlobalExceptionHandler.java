package com.nexus.shared.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.nexus.modules")
@Slf4j
@io.swagger.v3.oas.annotations.Hidden
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String getMessage(String code) {
        try {
            return messageSource.getMessage(code, null, code, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            log.warn("Erro ao buscar mensagem para código: {}", code, e);
            return code;
        }
    }

    private String getMessage(String code, Object... args) {
        try {
            return messageSource.getMessage(code, args, code, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            log.warn("Erro ao buscar mensagem para código: {}", code, e);
            return code;
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = getMessage(error.getDefaultMessage());
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("validation.error")
                .message(getMessage("validation.error.message"))
                .details(errors)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access Denied: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("access.denied")
                .message("Acesso negado. Você não tem permissão para realizar esta ação. Role necessária: GESTOR")
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        // Não tratar AccessDeniedException aqui, já tem handler específico
        if (ex instanceof AccessDeniedException) {
            return handleAccessDeniedException((AccessDeniedException) ex);
        }
        
        log.error("RuntimeException: ", ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("runtime.error")
                .message(getMessage("runtime.error.message", ex.getMessage()))
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // Não interceptar erros do SpringDoc
        if (ex.getClass().getName().contains("springdoc") || 
            ex.getMessage() != null && ex.getMessage().contains("springdoc")) {
            throw new RuntimeException(ex);
        }
        
        log.error("Exception: ", ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("internal.error")
                .message(getMessage("internal.error.message"))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}


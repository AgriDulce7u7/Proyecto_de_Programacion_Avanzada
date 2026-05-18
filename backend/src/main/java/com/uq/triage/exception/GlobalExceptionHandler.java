package com.uq.triage.exception;

import com.uq.triage.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidacion(MethodArgumentNotValidException ex) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.of(400, "Bad Request", errores));
    }

    @ExceptionHandler(SolicitudNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleSolicitudNotFound(SolicitudNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseDTO.of(404, "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(ResponsableNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResponsableNotFound(ResponsableNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseDTO.of(404, "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(ResponsableInactivoException.class)
    public ResponseEntity<ErrorResponseDTO> handleInactivo(ResponsableInactivoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.of(400, "Bad Request", ex.getMessage()));
    }

    @ExceptionHandler(TransicionInvalidaException.class)
    public ResponseEntity<ErrorResponseDTO> handleTransicion(TransicionInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponseDTO.of(422, "Unprocessable Entity", ex.getMessage()));
    }

    @ExceptionHandler(SolicitudCerradaException.class)
    public ResponseEntity<ErrorResponseDTO> handleCerrada(SolicitudCerradaException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponseDTO.of(422, "Unprocessable Entity", ex.getMessage()));
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponseDTO> handleCredenciales(CredencialesInvalidasException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDTO.of(401, "Unauthorized", ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleForbidden(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponseDTO.of(403, "Forbidden", "No tiene permisos para esta operacion."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenerico(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDTO.of(500, "Internal Server Error", "Error interno: " + ex.getMessage()));
    }
}

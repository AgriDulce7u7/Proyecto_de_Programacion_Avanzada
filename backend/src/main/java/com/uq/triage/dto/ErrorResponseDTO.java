package com.uq.triage.dto;
import lombok.AllArgsConstructor; import lombok.Getter;
import java.time.LocalDateTime;
@Getter @AllArgsConstructor
public class ErrorResponseDTO {
    private int status; private String error; private String mensaje; private LocalDateTime timestamp;
    public static ErrorResponseDTO of(int status, String error, String mensaje) {
        return new ErrorResponseDTO(status, error, mensaje, LocalDateTime.now());
    }
}

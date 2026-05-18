package com.uq.triage.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter; import lombok.Setter;
@Getter @Setter
public class LoginRequestDTO {
    @NotBlank private String correo;
    @NotBlank private String password;
}

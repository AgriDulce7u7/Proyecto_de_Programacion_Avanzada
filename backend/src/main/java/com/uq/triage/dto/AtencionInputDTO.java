package com.uq.triage.dto;
import jakarta.validation.constraints.*;
import lombok.Getter; import lombok.Setter;
@Getter @Setter
public class AtencionInputDTO {
    @NotBlank @Size(min = 10, max = 1000) private String observacion;
}

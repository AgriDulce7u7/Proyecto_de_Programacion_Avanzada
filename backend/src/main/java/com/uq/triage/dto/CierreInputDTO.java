package com.uq.triage.dto;
import com.uq.triage.enums.ResolucionCierre;
import jakarta.validation.constraints.*;
import lombok.Getter; import lombok.Setter;
@Getter @Setter
public class CierreInputDTO {
    @NotNull private ResolucionCierre resolucion;
    @NotBlank @Size(min = 10, max = 2000) private String observacionCierre;
}

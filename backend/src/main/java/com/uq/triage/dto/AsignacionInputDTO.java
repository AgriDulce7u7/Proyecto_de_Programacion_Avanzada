package com.uq.triage.dto;
import jakarta.validation.constraints.*;
import lombok.Getter; import lombok.Setter;
@Getter @Setter
public class AsignacionInputDTO {
    @NotNull(message = "El ID del responsable es obligatorio")
    private Long responsableId;
    @Size(max = 1000) private String comentario;
}

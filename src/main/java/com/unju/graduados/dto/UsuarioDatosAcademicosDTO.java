package com.unju.graduados.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioDatosAcademicosDTO {
    private Long usuarioId;

    //Relaciones
    private Long idUniversidad;
    @NotNull(message = "La facultad es obligatoria") // Asegura que el cliente envíe el ID
    private Long idFacultad;
    private Long idCarrera;
    private Long idColacion;

    private String especializaciones;
    private String posgrado;
    private String idiomas;
    private String matricula;   // <-- nuevo campo
    private String intereses;   // <-- nuevo campo
    private String observaciones;
    private Boolean tituloVerificado;
}

package com.unju.graduados.dto;

import lombok.Data;

@Data
public class UsuarioDatosAcademicosDTO {
    private Long usuarioId;

    //Relaciones
    private Long idUniversidad;
    private Long idFacultad;
    private Long idCarrera;
    private Long idColacion;

    private String especializaciones;
    private Boolean posgrado;
    private String idiomas;
    private String matricula;   // <-- nuevo campo
    private String intereses;   // <-- nuevo campo
    private String observaciones;
    private Boolean tituloVerificado;
}

package com.unju.graduados.dto;

import com.unju.graduados.repositories.projections.UsuarioInfoProjection;
import lombok.Data;
import java.util.List;

@Data
public class UsuarioPerfilDto {

    private Long id;
    private String dni;
    private String apellido;
    private String nombre;
    private String email;
    private List<PerfilEstadoDto> perfiles;

    /**
     * Constructor que acepta la proyección IUsuarioInfo (o UsuarioInfoDTO)
     * para inicializar los campos básicos.
     */
    public UsuarioPerfilDto(UsuarioInfoProjection userInfo) {
        this.id = userInfo.getId();
        this.dni = userInfo.getDni();
        this.apellido = userInfo.getApellido();
        this.nombre = userInfo.getNombre();
        this.email = userInfo.getEmail();
    }
    public UsuarioPerfilDto() {
    }

    @Data
    public static class PerfilEstadoDto {
        private Long id;
        private String nombrePerfil;
        private boolean asignado; // Flag para el checkbox en Thymeleaf
    }
}

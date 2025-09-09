package com.unju.graduados.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroDTO {
    // Usuario
    private String nombre;
    private String apellido;
    private Long dni;
    private String email;

    // Login
    private String username;
    private String password;

    // Datos académicos
    private Long universidadId;
    private Long facultadId;
    private Long carreraId;
    private Integer anioColacion;
}


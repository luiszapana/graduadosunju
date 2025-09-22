package com.unju.graduados.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroDTO {
    private Long id;

    // ===== Usuario =====
    private String nombre;
    private String apellido;
    private Long dni;
    private String email;
    private LocalDate fechaNacimiento;
    private String telefono;
    private String celular;
    private MultipartFile avatar;

    // ===== Login =====
    private String username;
    private String password;

    // ===== Dirección =====
    private Long provinciaId;
    private String localidad;
    private String domicilio;

    // ===== Datos académicos =====
    private Long universidadId;
    private Long facultadId;
    private Long carreraId;
    private Integer anioColacion;
}



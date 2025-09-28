package com.unju.graduados.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class AltaGraduadoAdminDTO {

    // ============================================
    // 1. Datos para UsuarioLogin (Usuario y Password)
    // ============================================
    @NotBlank
    @Email
    private String email; // Será el campo 'usuario' en UsuarioLogin

    // El password será el DNI (se requiere DNI para llenarlo)

    // ============================================
    // 2. Datos para Usuario (Personales)
    // ============================================
    @NotNull
    private Long dni;
    @NotBlank
    private String apellido;
    @NotBlank
    private String nombre;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;

    // Contacto
    private String telefono;
    private String celular;

    // Dirección (usaremos Ids, no el DTO completo)
    @NotNull
    private Long provinciaId;
    private String localidad;
    private String domicilio;

    // Foto de perfil y tipo de usuario se ignoran en el alta interna.

    // ============================================
    // 3. Datos para UsuarioDatosAcademicos
    // ============================================
    @NotNull
    private Long idUniversidad = 1L; // Por defecto UNJu
    @NotNull
    private Long idFacultad;
    @NotNull
    private Long idCarrera;

    private Long idColacion; // Puede ser null
    private String matricula;
    private String intereses;
    private String especializaciones;
    private String idiomas;
    private String posgrado;

    // Datos de la empresa (Opcional, se puede añadir después)
}

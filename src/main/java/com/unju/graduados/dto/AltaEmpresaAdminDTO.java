package com.unju.graduados.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

@Data
public class AltaEmpresaAdminDTO {

    // ... (Datos de Usuario y Dirección — Omitidos por brevedad, son idénticos a los de Graduado) ...

    @Email(message = "Formato de email incorrecto.")
    @NotBlank(message = "El Email principal es obligatorio.")
    private String email; // Email de UsuarioLogin

    @NotNull(message = "El DNI es obligatorio")
    private String dni;
    @NotBlank(message = "El Apellido es obligatorio.")
    private String apellido;
    @NotBlank(message = "El Nombre es obligatorio.")
    private String nombre;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    private String telefono; // Teléfono personal (usuario)
    private String celular;

    @NotNull(message = "La Provincia es obligatoria.")
    private Long provinciaId;
    @NotBlank(message = "La Localidad es obligatoria.")
    private String localidad;
    @NotBlank(message = "El Domicilio es obligatorio.")
    private String domicilio;

    // ============================================
    // 3. Datos para UsuarioDatosEmpresa (AJUSTADO A LA IMAGEN)
    // ============================================

    @NotBlank(message = "La Razón Social es obligatoria.")
    private String razonSocial;

    @NotBlank(message = "El CUIT es obligatorio.")
    private String cuit;

    @NotBlank(message = "La Dirección de la empresa es obligatoria.")
    private String direccion;

    // El email de la empresa es diferente al email de login.
    private String emailEmpresa;

    private String telefonoEmpresa; // Teléfono de la empresa

    // Campo 'imagen bytea' en BD -> Usamos MultipartFile para formulario.
    private MultipartFile imagenFile;
}

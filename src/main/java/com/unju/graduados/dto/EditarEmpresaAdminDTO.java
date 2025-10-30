package com.unju.graduados.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class EditarEmpresaAdminDTO {

    // ============================================
    // 1. ID del Usuario (Obligatorio para la edición)
    // ============================================
    @NotNull(message = "El ID del anunciante a editar es obligatorio.")
    private Long id;

    // ... (Datos de Usuario y Dirección — Omitidos por brevedad, son idénticos a los de Graduado) ...

    @NotBlank(message = "El Email es obligatorio.")
    @Email(message = "Formato de email incorrecto.")
    private String email;
    @NotNull(message = "El DNI es obligatorio")
    private String dni;
    @NotBlank(message = "El Apellido es obligatorio.")
    private String apellido;
    @NotBlank(message = "El Nombre es obligatorio.")
    private String nombre;
    private LocalDate fechaNacimiento;
    private String telefono;
    private String celular;
    @NotNull(message = "La Provincia es obligatoria.")
    private Long provinciaId;
    @NotBlank(message = "La Localidad es obligatoria.")
    private String localidad;
    @NotBlank(message = "El Domicilio es obligatorio.")
    private String domicilio;

    // ============================================
    // 4. Datos para UsuarioDatosEmpresa (AJUSTADO A LA IMAGEN)
    // ============================================

    @NotBlank(message = "La Razón Social es obligatoria.")
    private String razonSocial;

    @NotBlank(message = "El CUIT es obligatorio.")
    private String cuit;

    @NotBlank(message = "La Dirección de la empresa es obligatoria.")
    private String direccion;

    private String emailEmpresa;
    private String telefonoEmpresa;

    // Campo para DEVOLVER la imagen actual desde la BD (bytea -> byte[])
    private byte[] imagenActual;

    // Campo para RECIBIR una nueva imagen desde el formulario
    private MultipartFile imagenFileNueva;
}

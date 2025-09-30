package com.unju.graduados.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EditarGraduadoAdminDTO {
    private Long id; // ID del usuario que se edita

    @NotBlank
    @Email
    private String email;

    @NotNull
    private Long dni;

    @NotBlank
    private String apellido;

    @NotBlank
    private String nombre;

    private LocalDate fechaNacimiento;
    private String telefono;
    private String celular;

    @NotNull
    private Long provinciaId;
    private String localidad;
    private String domicilio;

    @NotNull
    private Long idUniversidad = 1L;
    @NotNull
    private Long idFacultad;
    @NotNull
    private Long idCarrera;

    private Long idColacion;
    private String matricula;
    private String intereses;
    private String especializaciones;
    private String idiomas;
    private String posgrado;

    @NotNull
    private Boolean tituloVerificado = false;
}


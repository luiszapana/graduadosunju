package com.unju.graduados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Set;

@Data
public class AnuncioDTO {
    private Long id;
    @NotBlank
    private String titulo;
    @NotBlank
    @Size(max = 4000)
    private String contenido;
    private String lugar;
    private String mailsReenvio;
    private Long idEmpresa;
    private ZonedDateTime duracionDesde;
    private ZonedDateTime duracionHasta;
    private ZonedDateTime fechaRegistro;
    private Boolean enviado;
    private ZonedDateTime fechaEnvio;
    private String mailContacto;
    private Long telefonoContacto;
    private String especializaciones;
    private String mailsEspecificos;
    private Long tipoId;
    private Set<Long> carrerasIds;
}

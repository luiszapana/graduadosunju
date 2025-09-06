package com.unju.graduados.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "anuncio")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Anuncio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    @Column(length = 4000)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo")//tipo_id
    private AnuncioTipo tipoAnuncio;

    @ManyToMany
    @JoinTable(name = "anuncio_carreras",
            joinColumns = @JoinColumn(name = "anuncio_id"),//anuncio_id
            inverseJoinColumns = @JoinColumn(name = "carreras_id"))
    @Builder.Default
    private Set<Carrera> carreras = new HashSet<>();
}

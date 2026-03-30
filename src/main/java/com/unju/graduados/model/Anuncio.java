package com.unju.graduados.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "anuncio_generator")
    @SequenceGenerator(
            name = "anuncio_generator",
            sequenceName = "anuncio_seq",
            allocationSize = 1
    )
    private Long id;

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 100, message = "El título debe tener menos de 100 caracteres")
    private String titulo;

    @NotBlank(message = "El contenido del anuncio no puede estar vacío")
    @Column(columnDefinition = "TEXT")
    private String contenido;

    private String lugar;

    @Column(name = "mails_reenvio")
    private String mailsReenvio;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Column(name = "duracion_desde")
    private ZonedDateTime duracionDesde;

    @Column(name = "duracion_hasta")
    private ZonedDateTime duracionHasta;

    @Column(name = "fecha_registro")
    private ZonedDateTime fechaRegistro;

    private Boolean enviado;

    @Column(name = "fecha_envio")
    private ZonedDateTime fechaEnvio;

    @Column(name = "mail_contacto")
    private String mailContacto;

    @Column(name = "telefono_contacto")
    private Long telefonoContacto;

    private String especializaciones;

    @Column(name = "mails_especificos")
    private String mailsEspecificos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo")
    private AnuncioTipo tipoAnuncio;

    @ManyToMany
    @JoinTable(
            name = "anuncio_carreras",
            joinColumns = @JoinColumn(
                    name = "anuncio_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "carreras_id",
                    referencedColumnName = "id"
            )
    )
    @Builder.Default
    private Set<Carrera> carreras = new HashSet<>();
}
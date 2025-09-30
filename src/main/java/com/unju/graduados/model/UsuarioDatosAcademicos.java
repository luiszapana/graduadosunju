package com.unju.graduados.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "usuario_datos_academicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDatosAcademicos implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_datos_academicos_seq")
    @SequenceGenerator(
            name = "usuario_datos_academicos_seq",
            sequenceName = "usuario_datos_academicos_seq",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_universidad")
    private Universidad universidad;

    @ManyToOne
    @JoinColumn(name = "id_facultad")
    private Facultad facultad;

    @ManyToOne
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    @Column(name = "flag_desuscribir")
    private Boolean flagDesuscribir;

    @Column(name = "titulo_verificado")
    @NotNull
    private Boolean tituloVerificado = false; // valor por defecto

    private String especializaciones;
    private String posgrado;
    private String idiomas;
    private String matricula;
    private String intereses;

    @Column(name = "verificado_por")
    private Long verificadoPor;

    @Column(name = "verificado_fecha")
    private ZonedDateTime verificadoFecha;

    @OneToOne(cascade={CascadeType.REFRESH})
    @JoinColumn(name="id_colacion", referencedColumnName="id")
    private Colacion colacion;

    private String observaciones;

    // Relación con Usuario (propietario aquí)
    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}
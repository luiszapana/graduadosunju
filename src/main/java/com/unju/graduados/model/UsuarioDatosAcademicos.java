package com.unju.graduados.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "usuario_datos_academicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDatosAcademicos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private Boolean flag_descubrir;
    private Boolean titulo_verificado;
    private String especializaciones;
    private Boolean posgrado;
    private String idiomas;
    private Long verificado_por;

    private ZonedDateTime verificado_fecha;
    private Long id_colacion;
    private String observaciones;

    // Relación con Usuario (propietario aquí)
    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}

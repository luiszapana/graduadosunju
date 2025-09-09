package com.unju.graduados.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "localidad")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Localidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoria;

    @Column(name = "centroide_lat")
    private String centroideLat;

    @Column(name = "centroide_lon")
    private String centroideLon;

    private String nombre;
    private String fuente;
    private String funcion;

    @Column(name = "municipio_id")
    private Long municipioId;

    @Column(name = "municipio_nombre")
    private String municipioNombre;

    @Column(name = "departamento_id")
    private Long departamentoId;

    @Column(name = "departamento_nombre")
    private String departamentoNombre;

    @Column(name = "provincia_id")
    private Long provinciaId;

    @Column(name = "provincia_nombre")
    private String provinciaNombre;
}


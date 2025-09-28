package com.unju.graduados.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "provincia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Provincia {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "iso_id")
    private String isoId;

    private String categoria;

    @Column(name = "centroide_lat")
    private String centroideLat;

    @Column(name = "centroide_lon")
    private String centroideLon;

    private String fuente;

    @Column(name = "iso_nombre")
    private String isoNombre;

    private String nombre;

    @Column(name = "nombre_completo")
    private String nombreCompleto;
}

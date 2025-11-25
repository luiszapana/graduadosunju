package com.unju.graduados.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "carrera")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Carrera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String titulo;

    @ManyToOne(fetch = FetchType.EAGER) // Cambio LAZY a EAGER para altas de anuncios
    @JoinColumn(name = "id_facultad")
    private Facultad facultad;

    @ManyToMany(mappedBy = "carreras")
    @Builder.Default
    private Set<Anuncio> anuncios = new HashSet<>();
}

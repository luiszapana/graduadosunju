package com.unju.graduados.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facultad")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Facultad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String etiqueta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_universidad")
    private Universidad universidad;

    @OneToMany(mappedBy = "facultad")
    @Builder.Default
    private List<Carrera> carreras = new ArrayList<>();
}

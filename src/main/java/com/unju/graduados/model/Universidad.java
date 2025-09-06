package com.unju.graduados.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "universidad")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Universidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @OneToMany(mappedBy = "universidad")
    @Builder.Default
    private List<Facultad> facultades = new ArrayList<>();
}

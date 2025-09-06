package com.unju.graduados.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "perfil")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String perfil;
    private Integer prioridad;
    private Boolean visibleWeb;

    @ManyToMany(mappedBy = "perfiles")
    @Builder.Default
    private Set<UsuarioLogin> usuarios = new HashSet<>();
}

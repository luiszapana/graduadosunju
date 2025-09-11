package com.unju.graduados.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "perfil")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "perfil", nullable = false)
    private String perfil;

    @Column(name = "prioridad", nullable = false)
    private Integer prioridad;

    @Column(name = "visible_web")
    private Boolean visibleWeb;

    @ManyToMany(mappedBy = "perfiles")
    @Builder.Default
    private Set<UsuarioLogin> usuarios = new HashSet<>();

    // Métodos helper
    public boolean isModerador() {
        return id != null && id.equals(2L);
    }

    public boolean isAdmin() {
        return id != null && id.equals(3L);
    }

    @Override
    public String toString() {
        return "Perfil [id=" + id + ", prioridad=" + prioridad + ", perfil=" + perfil + ", visibleWeb=" + visibleWeb + "]";
    }
}
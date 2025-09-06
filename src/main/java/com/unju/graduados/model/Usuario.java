package com.unju.graduados.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long dni;
    private String apellido;
    private String nombre;
    private ZonedDateTime fechaNacimiento;
    private String email;
    private Long telefono;
    private Long celular;

    @Lob
    private byte[] imagen;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UsuarioLogin> logins = new ArrayList<>();
}

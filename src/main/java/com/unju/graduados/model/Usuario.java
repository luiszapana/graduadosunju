package com.unju.graduados.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="usuario_seq")
    @SequenceGenerator(name="usuario_seq", sequenceName="usuario_seq", allocationSize=1)
    private Long id;

    private Long dni;
    private String apellido;
    private String nombre;
    @Column(name = "fecha_nacimiento")
    private ZonedDateTime fechaNacimiento;
    private String email;
    private String telefono;
    private String celular;

    //@Lob
    @Column(columnDefinition = "bytea")
    private byte[] imagen;

    // Relación con logins: gestionada por id en UsuarioLogin, no por asociación JPA directa
    @Transient
    private List<UsuarioLogin> logins = new ArrayList<>();

    // Relación con datos académicos
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UsuarioDatosAcademicos datosAcademicos;

    // Relación con datos de empresa
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UsuarioDatosEmpresa datosEmpresa;

    // Relación con dirección
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UsuarioDireccion direccion;
}


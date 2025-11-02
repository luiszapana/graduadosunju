package com.unju.graduados.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
// Hemos excluido datosAcademicos, datosEmpresa, y direccion de @ToString, lo cual es correcto si no son @Transient
@ToString(exclude = {"imagen", "logins"})
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="usuario_seq")
    @SequenceGenerator(name="usuario_seq", sequenceName="usuario_seq", allocationSize=1)
    private Long id;

    @NotNull(message = "El DNI es obligatorio")
    @Column(nullable = false, unique = true) // Añadido unique=true para consistencia
    private String dni;
    @Column(nullable = false)
    private String apellido;
    @Column(nullable = false)
    private String nombre;
    @Column(name = "fecha_nacimiento")
    private ZonedDateTime fechaNacimiento;
    @Column(unique = true, nullable = false) // Añadido unique=true para consistencia
    private String email;
    private String telefono;
    private String celular;

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "bytea")
    private byte[] imagen;

    @Transient
    private List<UsuarioLogin> logins = new ArrayList<>();
}
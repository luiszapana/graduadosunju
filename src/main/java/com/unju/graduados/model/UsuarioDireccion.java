package com.unju.graduados.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario_direccion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDireccion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_direccion_seq")
    @SequenceGenerator(name = "usuario_direccion_seq", sequenceName = "usuario_direccion_seq", allocationSize = 1)
    private Long id;
    private String domicilio;

    // Clave foránea de Usuario.
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;
    // Objetos Provincia y Localidad:
    @ManyToOne
    @JoinColumn(name = "id_provincia")
    private Provincia provincia;
    @ManyToOne
    @JoinColumn(name = "id_localidad")
    private Localidad localidad;
}

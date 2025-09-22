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
    @SequenceGenerator(
            name = "usuario_direccion_seq",
            sequenceName = "usuario_direccion_seq",
            allocationSize = 1
    )
    private Long id;

    private String domicilio;

    // Relación con Usuario
    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_provincia")
    private Provincia provincia;

    @ManyToOne
    @JoinColumn(name = "id_localidad")
    private Localidad localidad;
}

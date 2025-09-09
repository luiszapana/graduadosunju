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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String domicilio;

    @ManyToOne
    @JoinColumn(name = "id_provincia")
    private Provincia provincia;

    @ManyToOne
    @JoinColumn(name = "id_localidad")
    private Localidad localidad;

    // Relación con Usuario
    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}

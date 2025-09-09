package com.unju.graduados.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario_datos_empresa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDatosEmpresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "razon_social")  // 👈 corregido
    private String razonSocial;
    private String direccion;
    private String cuit;
    @Lob
    private byte[] imagen;
    private String email;
    private String telefono;

    // Relación con Usuario
    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}

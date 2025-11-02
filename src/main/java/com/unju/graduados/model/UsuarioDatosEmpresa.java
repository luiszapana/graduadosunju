package com.unju.graduados.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_datos_empresa_seq")
    @SequenceGenerator(
            name = "usuario_datos_empresa_seq",
            sequenceName = "usuario_datos_empresa_seq",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "razon_social")
    @NotBlank(message = "La razón social es obligatoria")
    private String razonSocial;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    private String cuit; // opcional

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "bytea")
    private byte[] imagen;

    @Email(message = "Email de contacto inválido")
    @NotBlank(message = "El email de contacto es obligatorio")
    private String email;

    @NotBlank(message = "El teléfono de contacto es obligatorio")
    private String telefono;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;
}

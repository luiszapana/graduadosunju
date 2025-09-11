package com.unju.graduados.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuario_login")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioLogin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_usuario")
    private Long idUsuario; // FK hacia Usuario

    @Column(name = "usuario", nullable = false)
    private String usuario; // normalmente email

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "habilitado", nullable = false)
    private Boolean habilitado = false;

    @Column(name = "registro_completo", nullable = false)
    private Boolean registroCompleto = false;

    @Column(name = "codigo_verificacion")
    private String codigoVerificacion;

    @Column(name = "fecha_primer_login")
    private ZonedDateTime fechaPrimerLogin;

    @Column(name = "fecha_ultimo_login")
    private ZonedDateTime fechaUltimoLogin;

    @Column(name = "fecha_registro")
    private ZonedDateTime fechaRegistro;

    @Column(name = "id_registrador")
    private Long idRegistrador;

    // ============================================
    // Relación muchos-a-muchos con Perfil
    // ============================================
    @ManyToMany
    @JoinTable(
            name = "usuario_login_perfiles",
            joinColumns = @JoinColumn(name = "login_id"),       // FK hacia esta tabla
            inverseJoinColumns = @JoinColumn(name = "perfil_id") // FK hacia tabla perfil
    )
    @Builder.Default
    private Set<Perfil> perfiles = new HashSet<>();
}

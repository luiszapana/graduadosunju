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

    private String usuario; // email
    private String password;
    private Boolean habilitado;
    private Boolean registroCompleto;
    private String codigoVerificacion;
    private ZonedDateTime fechaPrimerLogin;
    private ZonedDateTime fechaUltimoLogin;
    private ZonedDateTime fechaRegistro;
    private Long idRegistrador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuarioRef;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_login_perfiles",
            joinColumns = @JoinColumn(name = "login_id"),
            inverseJoinColumns = @JoinColumn(name = "perfiles_id"))
    @Builder.Default
    private Set<Perfil> perfiles = new HashSet<>();
}

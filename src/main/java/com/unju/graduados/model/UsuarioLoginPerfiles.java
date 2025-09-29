package com.unju.graduados.model; // Ajusta el paquete si es necesario

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "usuario_login_perfiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioLoginPerfiles {

    // Usa @EmbeddedId para la clave primaria compuesta
    @EmbeddedId
    private UsuarioLoginPerfilesId id;

    // Relación con UsuarioLogin (Columna login_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("loginId") // Indica que usa el campo 'loginId' de la clave compuesta
    @JoinColumn(name = "login_id", insertable = false, updatable = false) // Columna real en la BD
    private UsuarioLogin login;

    // Relación con Perfil (Columna perfiles_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("perfilesId") // Indica que usa el campo 'perfilesId' de la clave compuesta
    @JoinColumn(name = "perfiles_id", insertable = false, updatable = false)
    private Perfil perfil;
}
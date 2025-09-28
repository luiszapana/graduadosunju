package com.unju.graduados.services;

import com.unju.graduados.model.UsuarioLogin;

import java.util.Optional;

public interface IUsuarioLoginService {
    Optional<UsuarioLogin> findByUsuario(String usuario);
    UsuarioLogin save(UsuarioLogin login);
}

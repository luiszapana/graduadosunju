package com.unju.graduados.service;

import com.unju.graduados.model.UsuarioLogin;

import java.util.Optional;

public interface IUsuarioLoginService {
    Optional<UsuarioLogin> findByUsuario(String usuario);
    UsuarioLogin save(UsuarioLogin login);
}

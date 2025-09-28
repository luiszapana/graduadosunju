package com.unju.graduados.services;

import com.unju.graduados.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByDni(Long dni);
    List<Usuario> findAll();
    void deleteById(Long id);
}

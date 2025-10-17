package com.unju.graduados.services;

import com.unju.graduados.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface IUsuarioBaseService {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    void deleteById(Long id);
    List<Usuario> findAll();
}

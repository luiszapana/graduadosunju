package com.unju.graduados.services;

import com.unju.graduados.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface IUsuarioBaseService {
    Optional<Usuario> findByNombreLogin(String nombreLogin);
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    void deleteById(Long id);
    List<Usuario> findAll();
}

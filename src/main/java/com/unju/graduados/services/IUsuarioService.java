package com.unju.graduados.services;

import com.unju.graduados.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByDni(Long dni);

    // 🔄 Lista completa (si la necesitas para otra cosa)
    List<Usuario> findAll();

    // 🆕 Lista paginada (para la administración)
    Page<Usuario> findAll(Pageable pageable);

    void deleteById(Long id);
}

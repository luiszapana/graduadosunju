package com.unju.graduados.repositories;

import com.unju.graduados.model.UsuarioDireccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUsuarioDireccionRepository extends JpaRepository<UsuarioDireccion, Long> {
    Optional<UsuarioDireccion> findByUsuarioId(Long usuarioId);
}
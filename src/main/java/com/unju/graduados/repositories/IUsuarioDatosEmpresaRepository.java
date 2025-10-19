package com.unju.graduados.repositories;

import com.unju.graduados.model.UsuarioDatosEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUsuarioDatosEmpresaRepository extends JpaRepository<UsuarioDatosEmpresa, Long> {

    /**
     * Busca la información de empresa asociada a un Usuario por el ID de ese Usuario.
     * Este método es crucial para manejar la relación @OneToOne.
     *
     * @param idUsuario El ID del usuario.
     * @return Optional que contiene los datos de empresa si existen.
     */
    Optional<UsuarioDatosEmpresa> findByIdUsuario(Long idUsuario);
}

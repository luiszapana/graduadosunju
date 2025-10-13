package com.unju.graduados.repositories;

import com.unju.graduados.model.UsuarioDatosAcademicos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional; // Necesario para el método findBy

/**
 * Interfaz DAO para la entidad UsuarioDatosAcademicos.
 * Extiende JpaRepository para obtener métodos CRUD básicos.
 */
public interface IUsuarioDatosAcademicosRepository extends JpaRepository<UsuarioDatosAcademicos, Long> {
    /**
     * Busca los datos académicos asociados al campo 'idUsuario' de la entidad.
     * @param idUsuario El ID del usuario.
     * @return Un Optional que contiene UsuarioDatosAcademicos si existe, o vacío.
     */
    Optional<UsuarioDatosAcademicos> findByIdUsuario(Long idUsuario);
}

package com.unju.graduados.model.repositories;

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
     * Busca los datos académicos asociados a un ID de usuario específico.
     * Spring Data JPA genera la implementación automáticamente.
     *
     * @param usuarioId El ID de la entidad Usuario.
     * @return Un Optional que contiene UsuarioDatosAcademicos si existe, o vacío.
     */
    Optional<UsuarioDatosAcademicos> findByUsuarioId(Long usuarioId);

    /**
     * Elimina el registro de datos académicos asociado a un ID de usuario específico.
     * La consulta se ejecuta sobre la entidad UsuarioDatosAcademicos.
     * Se asume que la relación en la entidad se llama 'usuario'.
     * @param usuarioId El ID de la entidad Usuario (la clave foránea id_usuario).
     */
    @Modifying // Indica que esta consulta va a modificar (eliminar) datos
    @Query("DELETE FROM UsuarioDatosAcademicos uda WHERE uda.usuario.id = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);
}

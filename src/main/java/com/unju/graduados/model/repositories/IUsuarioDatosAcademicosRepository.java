package com.unju.graduados.model.repositories;

import com.unju.graduados.model.UsuarioDatosAcademicos;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // Necesario para el método findBy

/**
 * Interfaz DAO para la entidad UsuarioDatosAcademicos.
 * Extiende JpaRepository para obtener métodos CRUD básicos.
 */
public interface IUsuarioDatosAcademicosRepository extends JpaRepository<UsuarioDatosAcademicos, Long> {

    // ----------------------------------------------------------------------
    //  MÉTODO 'SAVE'
    //  ----------------------------------------------------------------------
    //  El método save(T entity) ES HEREDADO de JpaRepository.
    //  No es necesario declararlo explícitamente aquí.
    //  Se puede llamar directamente desde el servicio (dao.save(entity)).

    /**
     * Busca los datos académicos asociados a un ID de usuario específico.
     * Spring Data JPA genera la implementación automáticamente.
     *
     * @param usuarioId El ID de la entidad Usuario.
     * @return Un Optional que contiene UsuarioDatosAcademicos si existe, o vacío.
     */
    Optional<UsuarioDatosAcademicos> findByUsuarioId(Long usuarioId);
}

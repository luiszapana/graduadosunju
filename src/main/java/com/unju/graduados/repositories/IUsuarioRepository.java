package com.unju.graduados.repositories;

import com.unju.graduados.model.Usuario;
import com.unju.graduados.repositories.projections.UsuarioInfoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query(value = "SELECT u.id, u.dni, u.apellido, u.nombre, u.celular, u.email, STRING_AGG(p.perfil, ', ') AS perfiles " +
            "FROM usuario u " +
            "JOIN usuario_login ul ON ul.id_usuario = u.id " +
            "LEFT JOIN usuario_login_perfiles ulp ON ulp.login_id = ul.id " + // Usamos LEFT JOIN aquí
            "LEFT JOIN perfil p ON p.id = ulp.perfiles_id " + // Y aquí, para AGREGAR TODOS los roles
            "LEFT JOIN usuario_datos_academicos uda ON uda.id_usuario = u.id " +
            // 🚨 El WHERE ahora solo filtra a los usuarios que *tienen* el perfil (1, 2 o 3) en alguna parte
            "WHERE EXISTS (SELECT 1 FROM usuario_login_perfiles ulp_filter WHERE ulp_filter.login_id = ul.id AND " +
            "    ((:perfilId IS NULL AND ulp_filter.perfiles_id IN (1, 2, 3)) OR (ulp_filter.perfiles_id = :perfilId))) " +
            "GROUP BY u.id, u.dni, u.apellido, u.nombre, u.celular, u.email " +
            "ORDER BY u.apellido ASC",

            // El countQuery se mantiene simple y solo verifica la existencia de 1, 2 o 3
            countQuery = "SELECT COUNT(DISTINCT u.id) FROM usuario u " +
                    "JOIN usuario_login ul ON ul.id_usuario = u.id " +
                    "JOIN usuario_login_perfiles ulp ON ulp.login_id = ul.id " +
                    "WHERE (:perfilId IS NULL AND ulp.perfiles_id IN (1, 2, 3)) OR (ulp.perfiles_id = :perfilId)",
            nativeQuery = true)
    Page<UsuarioInfoProjection> findUsuariosByPerfilId(@Param("perfilId") Long perfilId, Pageable pageable);
}
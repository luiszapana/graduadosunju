package com.unju.graduados.repositories;

import com.unju.graduados.model.UsuarioLoginPerfilesId;
import com.unju.graduados.model.UsuarioLoginPerfiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsuarioLoginPerfilesRepository extends JpaRepository<UsuarioLoginPerfiles, UsuarioLoginPerfilesId> {

    /**
     * Elimina todos los registros de perfiles asociados a un ID de login específico.
     * La consulta se ejecuta sobre la entidad JPA UsuarioLoginPerfiles.
     * @param loginId El ID de la entidad UsuarioLogin (la clave foránea login_id).
     */
    @Modifying
    @Query("DELETE FROM UsuarioLoginPerfiles ulp WHERE ulp.login.id = :loginId")
    void deleteByLoginId(@Param("loginId") Long loginId);
}

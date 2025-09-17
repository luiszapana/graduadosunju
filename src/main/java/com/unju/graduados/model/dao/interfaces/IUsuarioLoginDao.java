package com.unju.graduados.model.dao.interfaces;

import com.unju.graduados.model.UsuarioLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IUsuarioLoginDao extends JpaRepository<UsuarioLogin, Long> {
    Optional<UsuarioLogin> findByUsuario(String usuario);
    Optional<UsuarioLogin> findByCodigoVerificacion(String codigoVerificacion);

    @Query("""
        SELECT ul FROM UsuarioLogin ul
        LEFT JOIN FETCH ul.perfiles
        WHERE ul.usuario = :usuario
    """)
    Optional<UsuarioLogin> findByUsuarioConPerfiles(@Param("usuario") String usuario);
}

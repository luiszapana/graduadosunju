package com.unju.graduados.model.dao.interfaces;

import com.unju.graduados.model.UsuarioLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUsuarioLoginDao extends JpaRepository<UsuarioLogin, Long> {
    Optional<UsuarioLogin> findByUsuario(String usuario);
    Optional<UsuarioLogin> findByCodigoVerificacion(String codigoVerificacion);
}

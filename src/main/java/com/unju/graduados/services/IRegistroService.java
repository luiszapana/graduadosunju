package com.unju.graduados.services;

import com.unju.graduados.dto.RegistroCredencialesDTO;
import com.unju.graduados.dto.RegistroDTO;
import com.unju.graduados.dto.UsuarioDatosAcademicosDTO;
import com.unju.graduados.model.Usuario;
import com.unju.graduados.model.UsuarioDatosEmpresa;
import com.unju.graduados.model.UsuarioLogin;

import java.util.Optional;

public interface IRegistroService {
    void registrarNuevoUsuario(RegistroDTO dto);

    String registrarCredenciales(RegistroCredencialesDTO dto);

    Optional<UsuarioLogin> verificarToken(String token);

    Usuario completarDatosPersonales(Long loginId, RegistroDTO dto,  boolean esEgresado);

    void asignarPerfilPorTipo(Long loginId, boolean esEgresado);

    void guardarDatosAcademicos(Long usuarioId, UsuarioDatosAcademicosDTO dto);

    void guardarDatosEmpresa(Long usuarioId, UsuarioDatosEmpresa emp);

    void asignarPerfilesGraduadoYUsuario(Long loginId);

    void validarLoginUsuario(Long loginId, Long usuarioId);
}

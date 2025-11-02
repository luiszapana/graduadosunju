package com.unju.graduados.services;

import com.unju.graduados.dto.*;
import com.unju.graduados.model.Usuario;
import com.unju.graduados.model.UsuarioDatosEmpresa;
import com.unju.graduados.model.UsuarioLogin;

import java.util.Optional;

public interface IRegistroExternoService {
    void registrarCredenciales(RegistroCredencialesDTO dto);
    Optional<UsuarioLogin> verificarToken(String token);
    Usuario completarDatosPersonales(Long loginId, RegistroDTO dto,  boolean esEgresado);
    void asignarPerfilPorTipo(Long loginId, boolean esEgresado);
    void guardarDatosAcademicos(Long usuarioId, UsuarioDatosAcademicosDTO dto); //Remover este método de aqui.
    void asignarPerfilesGraduadoYUsuario(Long loginId);
    void validarLoginUsuario(Long loginId, Long usuarioId);

    // MÉTODO AÑADIDO PARA SOLUCIONAR EL ERROR DE COMPILACIÓN:
    void saveDatosEmpresa(Long usuarioId, UsuarioDatosEmpresa emp);
}
package com.unju.graduados.services;

import com.unju.graduados.dto.AltaGraduadoAdminDTO;
import com.unju.graduados.dto.EditarGraduadoAdminDTO;

public interface IGraduadoAdminService {
    /**
     * Procesa el alta de un graduado por parte de un administrador o moderador.
     * Crea UsuarioLogin, Usuario, UsuarioDatosAcademicos y asigna el perfil GRADUADO.
     * @param dto El DTO que contiene todos los datos combinados del formulario de administración.
     */
    void registrarAltaInternaGraduado(AltaGraduadoAdminDTO dto);
    EditarGraduadoAdminDTO obtenerGraduadoParaEdicion(Long id);
    void actualizarGraduado(Long id, EditarGraduadoAdminDTO dto);
}

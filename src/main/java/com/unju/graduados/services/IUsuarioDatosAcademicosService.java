package com.unju.graduados.services;

import com.unju.graduados.dto.UsuarioDatosAcademicosDTO;

public interface IUsuarioDatosAcademicosService {

    /**
     * Guarda o actualiza la información de datos académicos de un usuario.
     *
     * @param dto El objeto DTO que contiene los datos académicos a guardar.
     */
    void guardar(UsuarioDatosAcademicosDTO dto);

    /**
     * Carga los datos académicos de un usuario por su ID para propósitos de edición.
     *
     * @param usuarioId El ID del usuario.
     * @return El DTO de los datos académicos.
     */
    UsuarioDatosAcademicosDTO cargarParaEdicion(Long usuarioId); //
}

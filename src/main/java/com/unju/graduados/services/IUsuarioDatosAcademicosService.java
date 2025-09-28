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
     * Carga los datos académicos de un usuario específico para su edición.
     *
     * @param usuarioId El ID del usuario cuyos datos académicos se desean cargar.
     * @return UsuarioDatosAcademicosDTO con los datos cargados.
     * @throws RuntimeException Si los datos académicos no son encontrados.
     */
    UsuarioDatosAcademicosDTO cargarParaEdicion(Long usuarioId);
}

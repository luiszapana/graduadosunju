package com.unju.graduados.services;

import java.util.List;

public interface ICorreoService {

    /**
     * Procesa la lista de carreras target, identifica a los graduados y envía el anuncio.
     * Este proceso debe ejecutarse de manera asíncrona para no bloquear la aplicación.
     *
     * @param anuncioId ID del anuncio creado (para el registro en anuncio_enviados).
     * @param carrerasTarget Lista de IDs de las carreras seleccionadas para el anuncio.
     * @param tituloAnuncio Título del anuncio para usar en el asunto del correo.
     */
    void enviarAnuncioAGraduadosAsync(Long anuncioId, List<Long> carrerasTarget, String tituloAnuncio);

}

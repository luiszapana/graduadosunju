package com.unju.graduados.services;

import com.unju.graduados.dto.AnuncioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;

public interface IAnuncioService {
    Page<AnuncioDTO> listar(Long tipoId, ZonedDateTime desde, ZonedDateTime hasta, Pageable pageable);
    AnuncioDTO obtener(Long id);
    AnuncioDTO actualizar(Long id, AnuncioDTO dto);
    void eliminar(Long id);

    /**
     * Crea un nuevo anuncio, gestiona el targeting a carreras y dispara el envío de notificaciones.
     * * @param dto Datos del anuncio, incluyendo el targeting de carreras/facultades.
     * @param idUsuarioCreador ID del usuario que está creando el anuncio.
     */
    AnuncioDTO crear(AnuncioDTO dto, Long idUsuarioCreador);
}

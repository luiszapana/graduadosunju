package com.unju.graduados.service;

import com.unju.graduados.dto.AnuncioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;

public interface IAnuncioService {
    Page<AnuncioDTO> listar(Long tipoId, ZonedDateTime desde, ZonedDateTime hasta, Pageable pageable);
    AnuncioDTO obtener(Long id);
    AnuncioDTO crear(AnuncioDTO dto);
    AnuncioDTO actualizar(Long id, AnuncioDTO dto);
    void eliminar(Long id);
}

package com.unju.graduados.services;

import com.unju.graduados.dto.CarreraDTO; // 👈 NECESITAS ESTA IMPORTACIÓN
import com.unju.graduados.model.Carrera;
import java.util.List;

public interface ICarreraService {

    // Métodos existentes
    List<Carrera> findAll();
    Carrera findById(Long id);

    /**
     * Busca y filtra carreras por nombre (query) y por ID de facultad.
     * Retorna DTOs para ser consumidos por la API.
     * * @param query El texto para buscar en el nombre de la carrera.
     * @param facultadId El ID de la facultad para filtrar (puede ser nulo).
     * @return Lista de CarreraDTO.
     */
    List<CarreraDTO> buscarCarreras(String query, Long facultadId); // 👈 MÉTODO AÑADIDO
}

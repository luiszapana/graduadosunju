package com.unju.graduados.services.impl;

import com.unju.graduados.dto.CarreraDTO;
import com.unju.graduados.model.Carrera;
import com.unju.graduados.repositories.ICarreraRepository; // Asume este es tu DAO/Repository
import com.unju.graduados.mappers.ICarreraMapper; // Asume que tienes un mapper de Carrera

import com.unju.graduados.services.ICarreraService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service // 👈 ¡CRUCIAL! Indica a Spring que esta clase es un Bean de servicio.
@RequiredArgsConstructor
public class CarreraServiceImpl implements ICarreraService {

    // Dependencias necesarias (Inyectadas vía constructor por Lombok)
    private final ICarreraRepository carreraDao; // Necesitas el DAO para acceder a los datos
    private final ICarreraMapper carreraMapper; // Necesitas el Mapper para convertir Entidad -> DTO

    // --- Métodos de ICarreraService ---

    @Override
    public List<Carrera> findAll() {
        // Implementación simple para cumplir con la interfaz
        return carreraDao.findAll();
    }

    @Override
    public Carrera findById(Long id) {
        // Implementación simple para cumplir con la interfaz
        return carreraDao.findById(id).orElse(null); // Manejo básico, ajústalo si usas Optional en la interfaz
    }

    @Override
    public List<CarreraDTO> buscarCarreras(String query, Long facultadId) {
        List<Carrera> carreras;

        if (facultadId != null && query != null && !query.isEmpty()) {
            // Busca por Facultad y por texto
            carreras = carreraDao.findByFacultadIdAndNombreContainingIgnoreCase(facultadId, query);
        } else if (facultadId != null) {
            // Busca solo por Facultad
            carreras = carreraDao.findByFacultadId(facultadId);
        } else if (query != null && !query.isEmpty()) {
            // Busca solo por texto
            carreras = carreraDao.findByNombreContainingIgnoreCase(query);
        } else {
            // Sin filtros (Devuelve todas, o una lista limitada)
            carreras = carreraDao.findAll();
        }

        // Convierte las entidades a DTOs para el controlador API
        return carreras.stream()
                .map(carreraMapper::toDTO)
                .collect(Collectors.toList());
    }
}
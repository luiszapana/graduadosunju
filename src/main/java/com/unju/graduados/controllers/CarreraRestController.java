package com.unju.graduados.controllers;

import com.unju.graduados.dto.CarreraDTO;
import com.unju.graduados.model.Carrera;
import com.unju.graduados.model.dao.interfaces.ICarreraDao;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carreras")
@RequiredArgsConstructor
public class CarreraRestController {

    private final ICarreraDao carreraRepository;

    @GetMapping
    public List<CarreraDTO> getCarreras(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long facultadId) {

        List<Carrera> carreras;
        if (facultadId != null && query != null && !query.isBlank()) {
            carreras = carreraRepository.findByFacultadIdAndNombreContainingIgnoreCase(facultadId, query);
        } else if (facultadId != null) {
            carreras = carreraRepository.findByFacultadId(facultadId);
        } else if (query != null && !query.isBlank()) {
            carreras = carreraRepository.findByNombreContainingIgnoreCase(query);
        } else {
            carreras = carreraRepository.findAll();
        }

        return carreras.stream()
                .map(c -> new CarreraDTO(c.getId(), c.getNombre()))
                .toList();
    }
}


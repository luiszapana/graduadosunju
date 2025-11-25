package com.unju.graduados.services.impl;

import com.unju.graduados.model.Facultad;
import com.unju.graduados.repositories.IFacultadRepository;
import com.unju.graduados.services.IFacultadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException; // Para manejar el caso de no encontrado

@Service // ¡CRUCIAL! Esto la convierte en un bean de Spring
@RequiredArgsConstructor
public class FacultadServiceImpl implements IFacultadService {

    private final IFacultadRepository facultadRepository;

    @Override
    public List<Facultad> findAll() {
        return facultadRepository.findAll();
    }
    @Override
    public Facultad findById(Long id) {
        return facultadRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Facultad con ID " + id + " no encontrada."));
    }
}
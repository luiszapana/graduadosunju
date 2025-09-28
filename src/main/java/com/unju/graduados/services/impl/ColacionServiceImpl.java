package com.unju.graduados.services.impl;

import com.unju.graduados.model.Colacion;
import com.unju.graduados.model.repositories.IColacionRepository;
import com.unju.graduados.services.IColacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColacionServiceImpl implements IColacionService {

    private final IColacionRepository colacionRepository;


    @Override
    public Page<Colacion> findAll(Pageable pageable) {
        // En lugar de usar findAllByOrderByFechaColacionDesc(),
        // usamos el findAll(Pageable) nativo del repositorio.
        // Spring Data JPA automáticamente aplica la paginación y la ordenación si se configuran en el PageRequest.
        return colacionRepository.findAll(pageable);
    }

    @Override
    public Colacion findByAnioColacion(Long anioColacion) {
        return colacionRepository.findByAnioColacion(anioColacion).orElse(null);
    }

    @Override
    public List<Colacion> findByFacultadId(Long facultadId) {
        return colacionRepository.findAll()
                .stream()
                .filter(c -> c.getFacultad() != null && facultadId.equals(c.getFacultad().getId()))
                .toList();
    }

    @Override
    public Colacion findById(Long id) {
        return colacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colación no encontrada"));
    }

    @Override
    public Colacion save(Colacion colacion) {
        // NO seteamos fechaRegistro aquí, lo hace @PrePersist en la entidad
        return colacionRepository.save(colacion);
    }

    @Override
    public Colacion update(Long id, Colacion datos) {
        Colacion existente = findById(id);
        existente.setUniversidad(datos.getUniversidad());
        existente.setFacultad(datos.getFacultad());
        existente.setOrden(datos.getOrden());
        existente.setDescripcion(datos.getDescripcion());
        existente.setFechaColacion(datos.getFechaColacion());
        existente.setAnioColacion(datos.getAnioColacion());
        // fechaRegistro NO se toca en update
        return colacionRepository.save(existente);
    }

    @Override
    public void delete(Long id) {
        colacionRepository.deleteById(id);
    }

    @Override
    public List<Colacion> findAllList() {
        // Opción A: Usar tu método personalizado de ordenación (si existe)
        // return colacionRepository.findAllByOrderByFechaColacionDesc();

        // Opción B: Usar el método findAll() de JpaRepository (no ordenado por defecto)
        return colacionRepository.findAll();
    }
}

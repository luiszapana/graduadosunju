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

    private final IColacionRepository colacionDao;

    @Override
    public Page<Colacion> findAll(Pageable pageable) {
        // En lugar de usar findAllByOrderByFechaColacionDesc(),
        // usamos el findAll(Pageable) nativo del repositorio.
        // Spring Data JPA automáticamente aplica la paginación y la ordenación si se configuran en el PageRequest.
        return colacionDao.findAll(pageable);
    }

    @Override
    public Colacion findByAnioColacion(Long anioColacion) {
        return colacionDao.findByAnioColacion(anioColacion).orElse(null);
    }

    @Override
    public List<Colacion> findByFacultadId(Long facultadId) {
        return colacionDao.findAll()
                .stream()
                .filter(c -> c.getFacultad() != null && facultadId.equals(c.getFacultad().getId()))
                .toList();
    }

    @Override
    public Colacion findById(Long id) {
        return colacionDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Colación no encontrada"));
    }

    @Override
    public Colacion save(Colacion colacion) {
        // NO seteamos fechaRegistro aquí, lo hace @PrePersist en la entidad
        return colacionDao.save(colacion);
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
        return colacionDao.save(existente);
    }

    @Override
    public void delete(Long id) {
        colacionDao.deleteById(id);
    }
}

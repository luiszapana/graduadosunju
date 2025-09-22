package com.unju.graduados.service.impl;

import com.unju.graduados.model.Colacion;
import com.unju.graduados.model.dao.interfaces.IColacionDao;
import com.unju.graduados.service.IColacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColacionServiceImpl implements IColacionService {

    private final IColacionDao colacionDao;

    @Override
    public List<Colacion> findAll() {
        return colacionDao.findAll();
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
}

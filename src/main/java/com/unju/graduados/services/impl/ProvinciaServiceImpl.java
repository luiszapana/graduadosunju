package com.unju.graduados.services.impl;

import com.unju.graduados.model.Provincia;
import com.unju.graduados.model.repositories.IProvinciaRepository;
import com.unju.graduados.services.IProvinciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvinciaServiceImpl implements IProvinciaService {

    private final IProvinciaRepository provinciaDao;

    @Override
    public List<Provincia> findAll() {
        return provinciaDao.findAll();
    }

    @Override
    public Provincia findById(Long id) {
        return provinciaDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Provincia no encontrada"));
    }
}
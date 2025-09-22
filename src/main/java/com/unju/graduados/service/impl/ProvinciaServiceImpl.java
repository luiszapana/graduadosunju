package com.unju.graduados.service.impl;

import com.unju.graduados.model.Provincia;
import com.unju.graduados.model.dao.interfaces.IProvinciaDao;
import com.unju.graduados.service.IProvinciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvinciaServiceImpl implements IProvinciaService {

    private final IProvinciaDao provinciaDao;

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
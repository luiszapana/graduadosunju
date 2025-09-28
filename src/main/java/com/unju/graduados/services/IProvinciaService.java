package com.unju.graduados.services;

import com.unju.graduados.model.Provincia;

import java.util.List;

public interface IProvinciaService {
    List<Provincia> findAll();
    Provincia findById(Long id);
}

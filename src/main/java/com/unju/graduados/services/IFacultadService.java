package com.unju.graduados.services;

import com.unju.graduados.model.Facultad;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IFacultadService {
    List<Facultad> findAll();
    Facultad findById(Long id);
}

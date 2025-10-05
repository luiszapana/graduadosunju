package com.unju.graduados.services.impl;

import com.unju.graduados.model.AnuncioTipo;
import com.unju.graduados.repositories.ITipoAnuncioRepository;
import com.unju.graduados.services.ITipoAnuncioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoAnuncioServiceImpl implements ITipoAnuncioService {
    private final ITipoAnuncioRepository tipoDao;

    @Override
    public List<AnuncioTipo> listar() {
        return tipoDao.findAll();
        
    }
}

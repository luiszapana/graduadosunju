package com.unju.graduados.service.impl;

import com.unju.graduados.model.AnuncioTipo;
import com.unju.graduados.model.dao.interfaces.ITipoAnuncioDao;
import com.unju.graduados.service.ITipoAnuncioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoAnuncioServiceImpl implements ITipoAnuncioService {
    private final ITipoAnuncioDao tipoDao;

    @Override
    public List<AnuncioTipo> listar() {
        return tipoDao.findAll();
        
    }
}

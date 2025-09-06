package com.unju.graduados.service.impl;

import com.unju.graduados.dto.AnuncioDTO;
import com.unju.graduados.model.Anuncio;
import com.unju.graduados.model.AnuncioTipo;
import com.unju.graduados.model.Carrera;
import com.unju.graduados.model.dao.interfaces.IAnuncioDao;
import com.unju.graduados.model.dao.interfaces.ICarreraDao;
import com.unju.graduados.model.dao.interfaces.ITipoAnuncioDao;
import com.unju.graduados.service.IAnuncioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AnuncioServiceImpl implements IAnuncioService {
    private final IAnuncioDao anuncioDao;
    private final ITipoAnuncioDao tipoDao;
    private final ICarreraDao carreraDao;

    @Override
    public Page<AnuncioDTO> listar(Long tipoId, ZonedDateTime desde, ZonedDateTime hasta, Pageable pageable) {
        return anuncioDao.findByFilters(tipoId, desde, hasta, pageable).map(this::toDTO);
    }

    @Override
    public AnuncioDTO obtener(Long id) {
        return anuncioDao.findById(id).map(this::toDTO).orElse(null);
    }

    @Override
    public AnuncioDTO crear(AnuncioDTO dto) {
        Anuncio entity = toEntity(dto, new Anuncio());
        entity.setFechaRegistro(ZonedDateTime.now());
        return toDTO(anuncioDao.save(entity));
    }

    @Override
    public AnuncioDTO actualizar(Long id, AnuncioDTO dto) {
        Optional<Anuncio> opt = anuncioDao.findById(id);
        if (opt.isEmpty()) return null;
        Anuncio entity = toEntity(dto, opt.get());
        return toDTO(anuncioDao.save(entity));
    }

    @Override
    public void eliminar(Long id) {
        anuncioDao.deleteById(id);
    }

    private AnuncioDTO toDTO(Anuncio a) {
        AnuncioDTO dto = new AnuncioDTO();
        dto.setId(a.getId());
        dto.setTitulo(a.getTitulo());
        dto.setContenido(a.getContenido());
        dto.setLugar(a.getLugar());
        dto.setMailsReenvio(a.getMailsReenvio());
        dto.setIdEmpresa(a.getIdEmpresa());
        dto.setDuracionDesde(a.getDuracionDesde());
        dto.setDuracionHasta(a.getDuracionHasta());
        dto.setFechaRegistro(a.getFechaRegistro());
        dto.setEnviado(a.getEnviado());
        dto.setFechaEnvio(a.getFechaEnvio());
        dto.setMailContacto(a.getMailContacto());
        dto.setTelefonoContacto(a.getTelefonoContacto());
        dto.setEspecializaciones(a.getEspecializaciones());
        dto.setMailsEspecificos(a.getMailsEspecificos());
        dto.setTipoId(a.getTipoAnuncio() != null ? a.getTipoAnuncio().getId() : null);
        if (a.getCarreras() != null) {
            Set<Long> ids = new HashSet<>();
            a.getCarreras().forEach(c -> ids.add(c.getId()));
            dto.setCarrerasIds(ids);
        }
        return dto;
    }

    private Anuncio toEntity(AnuncioDTO dto, Anuncio a) {
        a.setTitulo(dto.getTitulo());
        a.setContenido(dto.getContenido());
        a.setLugar(dto.getLugar());
        a.setMailsReenvio(dto.getMailsReenvio());
        a.setIdEmpresa(dto.getIdEmpresa());
        a.setDuracionDesde(dto.getDuracionDesde());
        a.setDuracionHasta(dto.getDuracionHasta());
        a.setEnviado(dto.getEnviado());
        a.setFechaEnvio(dto.getFechaEnvio());
        a.setMailContacto(dto.getMailContacto());
        a.setTelefonoContacto(dto.getTelefonoContacto());
        a.setEspecializaciones(dto.getEspecializaciones());
        a.setMailsEspecificos(dto.getMailsEspecificos());
        if (dto.getTipoId() != null) {
            AnuncioTipo tipo = tipoDao.findById(dto.getTipoId()).orElse(null);
            a.setTipoAnuncio(tipo);
        } else {
            a.setTipoAnuncio(null);
        }
        if (dto.getCarrerasIds() != null) {
            Set<Carrera> carreras = new HashSet<>();
            dto.getCarrerasIds().forEach(id -> carreraDao.findById(id).ifPresent(carreras::add));
            a.setCarreras(carreras);
        }
        return a;
    }
}

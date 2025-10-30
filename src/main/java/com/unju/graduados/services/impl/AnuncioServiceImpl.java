package com.unju.graduados.services.impl;

import com.unju.graduados.model.Anuncio;
import com.unju.graduados.model.Carrera;
import com.unju.graduados.repositories.IAnuncioRepository;
import com.unju.graduados.repositories.ICarreraRepository;
import com.unju.graduados.repositories.ITipoAnuncioRepository;
import com.unju.graduados.dto.AnuncioDTO;
import com.unju.graduados.services.IAnuncioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.unju.graduados.exceptions.ResourceNotFoundException;

@Slf4j
@Service
public class AnuncioServiceImpl implements IAnuncioService {

    private final IAnuncioRepository anuncioDao;
    private final ITipoAnuncioRepository tipoDao;
    private final ICarreraRepository carreraDao;

    @Autowired
    public AnuncioServiceImpl(IAnuncioRepository anuncioDao, ITipoAnuncioRepository tipoDao, ICarreraRepository carreraDao) {
        this.anuncioDao = anuncioDao;
        this.tipoDao = tipoDao;
        this.carreraDao = carreraDao;
    }

    @Override
    public Page<AnuncioDTO> listar(Long tipoId, ZonedDateTime desde, ZonedDateTime hasta, Pageable pageable) {
        //log.info("*** Invocando IAnuncioDao.findByFilters con tipoId={}, fechaDesde={}, fechaHasta={} ***", tipoId, desde, hasta);
        Specification<Anuncio> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (tipoId != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipoAnuncio").get("id"), tipoId));
            }

            if (desde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaRegistro"), desde));
            }

            if (hasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaRegistro"), hasta));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        Page<Anuncio> anuncios = anuncioDao.findAll(spec, pageable);
        return anuncios.map(this::toDTO);
    }

    // Código corregido
    private AnuncioDTO toDTO(Anuncio anuncio) {
        AnuncioDTO dto = new AnuncioDTO();
        dto.setId(anuncio.getId());
        dto.setTitulo(anuncio.getTitulo());
        dto.setContenido(anuncio.getContenido());
        dto.setLugar(anuncio.getLugar());
        dto.setMailsReenvio(anuncio.getMailsReenvio());
        dto.setIdEmpresa(anuncio.getIdEmpresa());
        dto.setDuracionDesde(anuncio.getDuracionDesde());
        dto.setDuracionHasta(anuncio.getDuracionHasta());
        dto.setFechaRegistro(anuncio.getFechaRegistro());
        dto.setEnviado(anuncio.getEnviado());
        dto.setFechaEnvio(anuncio.getFechaEnvio());
        dto.setMailContacto(anuncio.getMailContacto());
        dto.setTelefonoContacto(anuncio.getTelefonoContacto());
        dto.setEspecializaciones(anuncio.getEspecializaciones());
        dto.setMailsEspecificos(anuncio.getMailsEspecificos());

        // Mapeo de la relación
        if (anuncio.getTipoAnuncio() != null) {
            dto.setTipoId(anuncio.getTipoAnuncio().getId());
        }

        // Mapeo de la relación ManyToMany
        dto.setCarrerasIds(anuncio.getCarreras().stream()
                .map(carrera -> carrera.getId())
                .collect(Collectors.toSet()));

        return dto;
    }

    @Override
    public AnuncioDTO obtener(Long id) {
        return anuncioDao.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Anuncio con ID " + id + " no encontrado"));
    }

    @Override
    public AnuncioDTO crear(AnuncioDTO anuncioDTO) {
        // 1. Convert the DTO to an entity
        Anuncio anuncio = toEntity(anuncioDTO);

        // 2. Save the entity to the database
        Anuncio savedAnuncio = anuncioDao.save(anuncio);

        // 3. Convert the saved entity back to a DTO and return it
        return toDTO(savedAnuncio);
    }

    // Código corregido
    private Anuncio toEntity(AnuncioDTO anuncioDTO) {
        Anuncio anuncio = new Anuncio();
        anuncio.setTitulo(anuncioDTO.getTitulo());
        anuncio.setContenido(anuncioDTO.getContenido());
        anuncio.setLugar(anuncioDTO.getLugar());
        anuncio.setMailsReenvio(anuncioDTO.getMailsReenvio());
        anuncio.setIdEmpresa(anuncioDTO.getIdEmpresa());
        anuncio.setDuracionDesde(anuncioDTO.getDuracionDesde());
        anuncio.setDuracionHasta(anuncioDTO.getDuracionHasta());
        anuncio.setFechaRegistro(anuncioDTO.getFechaRegistro());
        anuncio.setEnviado(anuncioDTO.getEnviado());
        anuncio.setFechaEnvio(anuncioDTO.getFechaEnvio());
        anuncio.setMailContacto(anuncioDTO.getMailContacto());
        anuncio.setTelefonoContacto(anuncioDTO.getTelefonoContacto());
        anuncio.setEspecializaciones(anuncioDTO.getEspecializaciones());
        anuncio.setMailsEspecificos(anuncioDTO.getMailsEspecificos());

        if (anuncioDTO.getTipoId() != null) {
            anuncio.setTipoAnuncio(tipoDao.findById(anuncioDTO.getTipoId()).orElse(null));
        }

        // Asignación de carreras
        Set<Carrera> carreras = new HashSet<>();
        for (Long carreraId : anuncioDTO.getCarrerasIds()) {
            carreraDao.findById(carreraId).ifPresent(carreras::add);
        }
        anuncio.setCarreras(carreras);

        return anuncio;
    }

    @Override
    public AnuncioDTO actualizar(Long id, AnuncioDTO dto) {
        Anuncio anuncio = anuncioDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anuncio con ID " + id + " no encontrado"));

        anuncio.setTitulo(dto.getTitulo());
        anuncio.setContenido(dto.getContenido());
        anuncio.setLugar(dto.getLugar());
        anuncio.setMailsReenvio(dto.getMailsReenvio());
        anuncio.setIdEmpresa(dto.getIdEmpresa());
        anuncio.setDuracionDesde(dto.getDuracionDesde());
        anuncio.setDuracionHasta(dto.getDuracionHasta());
        anuncio.setFechaRegistro(dto.getFechaRegistro());
        anuncio.setEnviado(dto.getEnviado());
        anuncio.setFechaEnvio(dto.getFechaEnvio());
        anuncio.setMailContacto(dto.getMailContacto());
        anuncio.setTelefonoContacto(dto.getTelefonoContacto());
        anuncio.setEspecializaciones(dto.getEspecializaciones());
        anuncio.setMailsEspecificos(dto.getMailsEspecificos());

        if (dto.getTipoId() != null) {
            anuncio.setTipoAnuncio(tipoDao.findById(dto.getTipoId()).orElse(null));
        }

        Set<Carrera> carreras = new HashSet<>();
        for (Long carreraId : dto.getCarrerasIds()) {
            carreraDao.findById(carreraId).ifPresent(carreras::add);
        }
        anuncio.setCarreras(carreras);

        return toDTO(anuncioDao.save(anuncio));
    }

    @Override
    public void eliminar(Long id) {
        // 1. Busca el anuncio por su ID para verificar si existe
        if (!anuncioDao.existsById(id)) {
            // Lanza una excepción si el anuncio no se encuentra
            throw new ResourceNotFoundException("Anuncio con ID " + id + " no encontrado");
        }

        // 2. Si el anuncio existe, elimínalo
        anuncioDao.deleteById(id);
    }
}
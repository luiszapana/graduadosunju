package com.unju.graduados.services.impl;

import com.unju.graduados.mappers.AnuncioMapper;
import com.unju.graduados.model.Anuncio;
import com.unju.graduados.model.Carrera;
import com.unju.graduados.repositories.*;
import com.unju.graduados.dto.AnuncioDTO;
import com.unju.graduados.services.IAnuncioService;
import com.unju.graduados.services.ICorreoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import com.unju.graduados.exceptions.ResourceNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnuncioServiceImpl implements IAnuncioService {

    private final IAnuncioRepository anuncioRepository;
    private final ITipoAnuncioRepository tipoAnuncioRepository;
    private final ICarreraRepository carreraRepository;
    private final ICorreoService correoService;
    private final AnuncioMapper anuncioMapper;

    @Override
    public Page<AnuncioDTO> listar(Long tipoId, ZonedDateTime desde, ZonedDateTime hasta, Pageable pageable) {
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
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
        Page<Anuncio> anuncios = anuncioRepository.findAll(spec, pageable);
        return anuncios.map(anuncioMapper::toDto);
    }

    @Override
    public AnuncioDTO obtener(Long id) {
        return anuncioRepository.findById(id)
                .map(anuncioMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Anuncio con ID " + id + " no encontrado"));
    }

    @Override
    public AnuncioDTO actualizar(Long id, AnuncioDTO dto) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anuncio con ID " + id + " no encontrado"));
        anuncioMapper.updateEntityFromDto(dto, anuncio);

        // 1. Actualización de TipoAnuncio
        if (dto.getTipoId() != null) {
            anuncio.setTipoAnuncio(tipoAnuncioRepository.findById(dto.getTipoId()).orElse(null));
        } else {
            anuncio.setTipoAnuncio(null);
        }

        // 2. Actualización de Carreras (Many-to-Many)
        Set<Carrera> carreras = new HashSet<>();
        for (Long carreraId : dto.getCarrerasIds()) {
            carreraRepository.findById(carreraId).ifPresent(carreras::add);
        }
        anuncio.setCarreras(carreras);
        return anuncioMapper.toDto(anuncioRepository.save(anuncio));
    }

    @Override
    @Transactional
    public AnuncioDTO crear(AnuncioDTO dto, Long idUsuarioCreador) {

        // 1. Mapear DTO a Entidad
        Anuncio nuevoAnuncio = anuncioMapper.toEntity(dto);
        nuevoAnuncio.setIdEmpresa(idUsuarioCreador);
        nuevoAnuncio.setFechaRegistro(ZonedDateTime.now());

        // 2. BUSCAR LAS CARRERAS Y ASIGNARLAS
        // Esto es lo que llena la tabla anuncio_carreras automáticamente
        if (dto.getCarrerasIds() != null && !dto.getCarrerasIds().isEmpty()) {
            Set<Carrera> carreras = new HashSet<>(carreraRepository.findAllById(dto.getCarrerasIds()));
            nuevoAnuncio.setCarreras(carreras);
        }

        // 3. Guardar el Anuncio
        // Al guardar 'nuevoAnuncio', JPA inserta en 'anuncio' y en 'anuncio_carreras'
        nuevoAnuncio = anuncioRepository.save(nuevoAnuncio);

        final Long anuncioId = nuevoAnuncio.getId();

        // 4. Disparar Correo
        if (dto.getCarrerasIds() != null && !dto.getCarrerasIds().isEmpty()) {
            List<Long> carrerasList = new ArrayList<>(dto.getCarrerasIds());
            correoService.enviarAnuncioAGraduadosAsync(
                    anuncioId,
                    carrerasList,
                    nuevoAnuncio.getTitulo(),
                    nuevoAnuncio.getContenido()
            );
        }

        return anuncioMapper.toDto(nuevoAnuncio);
    }

    @Override
    public void eliminar(Long id) {
        if (!anuncioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Anuncio con ID " + id + " no encontrado");
        }
        anuncioRepository.deleteById(id);
    }
}
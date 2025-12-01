package com.unju.graduados.services.impl;

import com.unju.graduados.mappers.AnuncioMapper;
import com.unju.graduados.model.Anuncio;
import com.unju.graduados.model.AnuncioCarrera;
import com.unju.graduados.model.Carrera;
import com.unju.graduados.repositories.*;
import com.unju.graduados.dto.AnuncioDTO;
import com.unju.graduados.services.IAnuncioService;
import com.unju.graduados.services.ICorreoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AnuncioServiceImpl implements IAnuncioService {

    private final IAnuncioRepository anuncioRepository;
    private final ITipoAnuncioRepository tipoAnuncioRepository;
    private final ICarreraRepository carreraRepository;
    private final IAnuncioCarreraRepository anuncioCarreraRepository;
    private final ICorreoService correoService;
    private final IGraduadoRepository graduadoRepository;
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

        // 2. Guardar el Anuncio Principal
        nuevoAnuncio = anuncioRepository.save(nuevoAnuncio);
        final Long anuncioId = nuevoAnuncio.getId();

        // 3. Manejar el Targeting
        Set<Long> carrerasTarget = dto.getCarrerasIds();
        if (carrerasTarget != null && !carrerasTarget.isEmpty()) {
            List<AnuncioCarrera> relaciones = carrerasTarget.stream()
                    .map(carreraId -> new AnuncioCarrera(anuncioId, carreraId))
                    .toList();
            anuncioCarreraRepository.saveAll(relaciones);

            // 4. Disparar Correo
            List<Long> carrerasList = carrerasTarget.stream().toList();
            correoService.enviarAnuncioAGraduadosAsync(
                    anuncioId,
                    carrerasList,
                    nuevoAnuncio.getTitulo(),
                    nuevoAnuncio.getContenido()
            );
            //correoService.enviarAnuncioAGraduadosAsync(anuncioId, carrerasList, nuevoAnuncio.getTitulo());
        } else {
            log.warn("Anuncio ID {} creado por usuario {} sin carreras target. No se enviará notificación por correo.", anuncioId, idUsuarioCreador);
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
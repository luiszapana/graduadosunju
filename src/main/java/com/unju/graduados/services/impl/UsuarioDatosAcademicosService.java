package com.unju.graduados.services.impl;

import com.unju.graduados.dto.UsuarioDatosAcademicosDTO;
import com.unju.graduados.model.UsuarioDatosAcademicos;
import com.unju.graduados.repositories.IUsuarioDatosAcademicosRepository;
import com.unju.graduados.mappers.UsuarioDatosAcademicosMapper;
import com.unju.graduados.services.IUsuarioDatosAcademicosService;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor; // ¡Nueva importación!

@Service
@RequiredArgsConstructor
public class UsuarioDatosAcademicosService implements IUsuarioDatosAcademicosService {

    private final UsuarioDatosAcademicosMapper mapper;
    private final IUsuarioDatosAcademicosRepository repository;

    @Override
    public void guardar(UsuarioDatosAcademicosDTO dto) {
        UsuarioDatosAcademicos entity = mapper.toEntity(dto);
        repository.save(entity);
    }

    @Override
    public UsuarioDatosAcademicosDTO cargarParaEdicion(Long usuarioId) {
        UsuarioDatosAcademicos entity = repository.findByIdUsuario(usuarioId)
                .orElseThrow(() -> new RuntimeException("Datos académicos no encontrados"));
        return mapper.toDTO(entity);
    }
}
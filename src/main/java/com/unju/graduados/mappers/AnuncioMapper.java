package com.unju.graduados.mappers;

import com.unju.graduados.dto.AnuncioDTO;
import com.unju.graduados.model.Anuncio;
import com.unju.graduados.model.Carrera;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AnuncioMapper {

    // 1. Mapeo de Entidad a DTO (para las respuestas de la API)
    @Mapping(target = "tipoId", source = "tipoAnuncio.id")
    @Mapping(target = "tipoNombre", source = "tipoAnuncio.descripcion")
    @Mapping(target = "carrerasIds", source = "carreras", qualifiedByName = "mapCarrerasToIds")
    AnuncioDTO toDto(Anuncio entity);

    // 2. Mapeo de DTO a Entidad (para crear/actualizar)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipoAnuncio", ignore = true)
    @Mapping(target = "carreras", ignore = true)
    Anuncio toEntity(AnuncioDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipoAnuncio", ignore = true)
    @Mapping(target = "carreras", ignore = true)
    void updateEntityFromDto(AnuncioDTO dto, @MappingTarget Anuncio anuncio);

    // 3. Método lógico para extraer los IDs de las carreras
    @Named("mapCarrerasToIds")
    default Set<Long> mapCarrerasToIds(Set<Carrera> carreras) {
        if (carreras == null) return null;
        return carreras.stream()
                .map(Carrera::getId)
                .collect(Collectors.toSet());
    }
}
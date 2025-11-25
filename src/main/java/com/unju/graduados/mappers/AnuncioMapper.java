package com.unju.graduados.mappers; // Asegúrate de usar el paquete correcto

import com.unju.graduados.dto.AnuncioDTO;
import com.unju.graduados.model.Anuncio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring") // MapStruct: Lo registra como un bean de Spring
public interface AnuncioMapper {
    Anuncio toEntity(AnuncioDTO dto);
    AnuncioDTO toDto(Anuncio entity);
    // Ignoramos el ID y las relaciones que manejas manualmente en el servicio (tipoAnuncio y carreras).
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipoAnuncio", ignore = true) // Ignorar la relación compleja
    @Mapping(target = "carreras", ignore = true) // Ignorar la relación compleja
    void updateEntityFromDto(AnuncioDTO dto, @MappingTarget Anuncio anuncio);
}
package com.unju.graduados.mappers;

import com.unju.graduados.dto.CarreraDTO;
import com.unju.graduados.model.Carrera;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICarreraMapper {

    /**
     * Convierte la entidad Carrera a su DTO correspondiente.
     */
    CarreraDTO toDTO(Carrera entity);
}
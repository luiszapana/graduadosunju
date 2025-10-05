package com.unju.graduados.mappers;

import com.unju.graduados.dto.UsuarioDatosAcademicosDTO;
import com.unju.graduados.model.UsuarioDatosAcademicos;

import com.unju.graduados.repositories.ICarreraRepository;
import com.unju.graduados.repositories.IFacultadRepository;
import com.unju.graduados.repositories.IUniversidadRepository;
import com.unju.graduados.repositories.IUsuarioRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
@RequiredArgsConstructor // ✅ genera constructor con los repos final
@NoArgsConstructor(force = true) // 👈 ¡AÑADE ESTO! Genera constructor sin argumentos (para MapStruct)
public abstract class UsuarioDatosAcademicosMapper {

    // Inyección por constructor gracias a Lombok
    protected final IFacultadRepository facultadDao;
    protected final ICarreraRepository carreraDao;
    protected final IUniversidadRepository universidadDao;
    protected final IUsuarioRepository usuarioDao;

    // 🔄 DTO → Entity
    @Mapping(target = "facultad", expression = "java(dto.getIdFacultad() != null ? facultadDao.findById(dto.getIdFacultad()).orElse(null) : null)")
    @Mapping(target = "carrera", expression = "java(dto.getIdCarrera() != null ? carreraDao.findById(dto.getIdCarrera()).orElse(null) : null)")
    @Mapping(target = "universidad", expression = "java(dto.getIdUniversidad() != null ? universidadDao.findById(dto.getIdUniversidad()).orElse(null) : null)")
    @Mapping(target = "usuario", expression = "java(dto.getUsuarioId() != null ? usuarioDao.findById(dto.getUsuarioId()).orElse(null) : null)")
    public abstract UsuarioDatosAcademicos toEntity(UsuarioDatosAcademicosDTO dto);

    // 🔄 Entity → DTO (para precargar formulario en edición)
    @Mapping(target = "idFacultad", expression = "java(entity.getFacultad() != null ? entity.getFacultad().getId() : null)")
    @Mapping(target = "idCarrera", expression = "java(entity.getCarrera() != null ? entity.getCarrera().getId() : null)")
    @Mapping(target = "idUniversidad", expression = "java(entity.getUniversidad() != null ? entity.getUniversidad().getId() : null)")
    @Mapping(target = "usuarioId", expression = "java(entity.getUsuario() != null ? entity.getUsuario().getId() : null)")
    @Mapping(target = "idColacion", expression = "java(entity.getColacion() != null ? entity.getColacion().getId() : null)")
    public abstract UsuarioDatosAcademicosDTO toDTO(UsuarioDatosAcademicos entity);

    // ✅ Conversión automática ZonedDateTime → LocalDate
    protected LocalDate map(ZonedDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate() : null;
    }

    // ✅ Conversión LocalDate → ZonedDateTime (si necesitás la inversa)
    protected ZonedDateTime map(LocalDate localDate) {
        return localDate != null ? localDate.atStartOfDay(ZoneId.systemDefault()) : null;
    }
}
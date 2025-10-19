package com.unju.graduados.services.impl;

import com.unju.graduados.dto.UsuarioPerfilDto;
import com.unju.graduados.exceptions.ResourceNotFoundException;
import com.unju.graduados.model.Perfil;
import com.unju.graduados.model.Usuario;
import com.unju.graduados.model.UsuarioLogin;
import com.unju.graduados.repositories.IUsuarioLoginRepository;
import com.unju.graduados.repositories.IUsuarioRepository;
import com.unju.graduados.repositories.IPerfilRepository;
import com.unju.graduados.repositories.projections.UsuarioInfoProjection;
import com.unju.graduados.services.IUsuarioPerfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioPerfilServiceImpl implements IUsuarioPerfilService {

    private final IUsuarioRepository usuarioRepository;
    private final IPerfilRepository perfilRepository;
    private final IUsuarioLoginRepository usuarioLoginRepository;

    @Override
    public Page<UsuarioInfoProjection> findUsuariosByPerfilId(Long perfilId, Pageable pageable) {
        return usuarioRepository.findUsuariosByPerfilId(perfilId, pageable);
    }

    @Override
    public UsuarioPerfilDto getUsuarioPerfiles(Long usuarioId) {
        // 1. Obtener el usuario (Entidad completa)
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        // 2. Obtener la entidad UsuarioLogin por el ID del Usuario
        UsuarioLogin login = usuarioLoginRepository.findByIdUsuario(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Acceso (Login) no encontrado para el Usuario ID: " + usuarioId));

        // 3. Crear el DTO y mapear campos (Aquí es donde se declara la variable 'dto')
        UsuarioPerfilDto dto = new UsuarioPerfilDto(); // <-- ¡AQUÍ ESTÁ LA DECLARACIÓN!

        // Mapeo manual de las propiedades que SÍ te interesan
        dto.setId(usuario.getId());
        dto.setDni(usuario.getDni());
        dto.setApellido(usuario.getApellido());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        // Se evita cualquier relación con UsuarioInfoProjectionDTO o tituloVerificado.

        // 4. Obtener perfiles asignados
        Set<Long> perfilesAsignadosIds = login.getPerfiles().stream()
                .map(Perfil::getId)
                .collect(Collectors.toSet());

        // 5. Obtener todos los perfiles disponibles
        List<Perfil> todosLosPerfiles = perfilRepository.findAll();

        // 6. Llenar la lista de estados de perfiles...
        List<UsuarioPerfilDto.PerfilEstadoDto> estados = todosLosPerfiles.stream()
                .map(p -> {
                    UsuarioPerfilDto.PerfilEstadoDto estado = new UsuarioPerfilDto.PerfilEstadoDto();
                    estado.setId(p.getId());
                    estado.setNombrePerfil(p.getPerfil());
                    estado.setAsignado(perfilesAsignadosIds.contains(p.getId()));
                    return estado;
                })
                .collect(Collectors.toList());

        dto.setPerfiles(estados);
        return dto;
    }

    @Override
    public void updateUsuarioPerfiles(Long usuarioId, List<Long> perfilIds) {

        // 1. Buscar la entidad UsuarioLogin por el ID del Usuario
        // (UsuarioLogin contiene el Set<Perfil> que queremos modificar)
        UsuarioLogin login = usuarioLoginRepository.findByIdUsuario(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Acceso (Login) no encontrado para el Usuario ID: " + usuarioId));

        // 2. Buscar las entidades Perfil correspondientes a los IDs seleccionados
        // findAllById devuelve una lista de entidades Perfil.
        List<Perfil> nuevosPerfilesList = perfilRepository.findAllById(perfilIds);

        // Convertir la lista a un Set<Perfil> para la relación de la entidad.
        Set<Perfil> nuevosPerfilesSet = nuevosPerfilesList.stream()
                .collect(Collectors.toSet());

        // 3. Establecer el nuevo conjunto de perfiles en la entidad UsuarioLogin
        // Esto reemplaza todos los perfiles antiguos por los perfiles recién seleccionados.
        login.setPerfiles(nuevosPerfilesSet);

        // 4. Guardar la entidad UsuarioLogin actualizada
        // Esto persiste el cambio en la tabla de relación (JOIN TABLE) en la base de datos.
        usuarioLoginRepository.save(login);
    }
}
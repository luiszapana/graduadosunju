package com.unju.graduados.services.impl;

import com.unju.graduados.model.Perfil;
import com.unju.graduados.repositories.IPerfilRepository; // Necesario para acceder a la tabla perfil
import com.unju.graduados.services.IPerfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerfilServiceImpl implements IPerfilService {

    // Inyecta el repositorio para acceder a la tabla 'perfil'
    private final IPerfilRepository perfilRepository;

    @Override
    public List<Perfil> getPerfilesParaAdministracion() {
        // En tu esquema, los perfiles de administración son 1 (USUARIO), 2 (MODERADOR) y 3 (ADMINISTRADOR).
        // El perfil 4 (GRADUADO) debe ser excluido del filtro de administración de roles.

        return perfilRepository.findAll().stream()
                .filter(p -> p.getId() != 4L) // Excluir el perfil GRADUADO (ID 4)
                .collect(Collectors.toList());
    }
    // Si IPerfilService tiene otros métodos, deben ser implementados aquí.
}
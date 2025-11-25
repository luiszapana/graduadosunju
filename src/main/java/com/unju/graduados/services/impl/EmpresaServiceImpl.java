package com.unju.graduados.services.impl;

import com.unju.graduados.model.Usuario;
import com.unju.graduados.model.UsuarioDatosEmpresa;
import com.unju.graduados.repositories.IEmpresaRepository;
import com.unju.graduados.repositories.IGraduadoRepository;
import com.unju.graduados.repositories.IUsuarioDatosEmpresaRepository;
import com.unju.graduados.services.IEmpresaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // O @Component, dependiendo de tu convención
@RequiredArgsConstructor
public class EmpresaServiceImpl implements IEmpresaService {

    private final IGraduadoRepository graduadoRepository;; // O IGraduadoRepository/IUsuarioBaseRepository según tu estructura final
    private final IUsuarioDatosEmpresaRepository datosEmpresaRepository;
    private final IEmpresaRepository empresaRepository;

    // ===========================================
    // ⬇️ Métodos heredados de IUsuarioBaseService (Para CRUD base)
    // ===========================================

    @Override
    public Usuario save(Usuario usuario) {
        return graduadoRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return graduadoRepository.findById(id);
    }

    @Override
    public List<Usuario> findAll() {
        return graduadoRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        graduadoRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void saveDatosEmpresa(Long usuarioId, UsuarioDatosEmpresa emp) {
        // 1. Verificar la existencia del usuario y obtener la entidad Usuario
        Usuario usuario = graduadoRepository.findById(usuarioId).orElseThrow(
                () -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId)
        );

        // 2. Buscar si ya existe una entidad de Datos Empresa para este usuario.
        UsuarioDatosEmpresa existingEmp = datosEmpresaRepository.findByIdUsuario(usuarioId)
                .orElse(new UsuarioDatosEmpresa()); // Si no existe, crea una nueva

        // 3. Copiar los datos
        existingEmp.setRazonSocial(emp.getRazonSocial());
        existingEmp.setDireccion(emp.getDireccion());
        existingEmp.setCuit(emp.getCuit());
        existingEmp.setImagen(emp.getImagen());
        existingEmp.setEmail(emp.getEmail());
        existingEmp.setTelefono(emp.getTelefono());
        existingEmp.setIdUsuario(usuarioId); // Asegurar el vínculo

        // 5. Guardar la entidad de empresa
        datosEmpresaRepository.save(existingEmp);
    }

    /**
     * Implementación requerida por IUsuarioBaseService.
     * Busca la entidad Usuario/Empresa por su nombre de login (email/username).
     */
    @Override
    public Optional<Usuario> findByNombreLogin(String nombreLogin) {
        // Asumiendo que las entidades de Empresa/Usuario comparten el campo 'email'
        // y que IEmpresaRepository maneja la entidad Usuario o tiene un método para buscar por email.
        return empresaRepository.findByEmail(nombreLogin);
    }
}


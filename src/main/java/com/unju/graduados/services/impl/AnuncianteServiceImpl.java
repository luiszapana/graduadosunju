package com.unju.graduados.services.impl;

import com.unju.graduados.model.Usuario;
import com.unju.graduados.model.UsuarioDatosEmpresa;
import com.unju.graduados.repositories.IGraduadoRepository;
import com.unju.graduados.repositories.IUsuarioDatosEmpresaRepository;
import com.unju.graduados.services.IAnuncianteService;
import com.unju.graduados.services.IUsuarioBaseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // O @Component, dependiendo de tu convención
@RequiredArgsConstructor
public class AnuncianteServiceImpl implements IAnuncianteService {

    private final IGraduadoRepository graduadoRepository;; // O IGraduadoRepository/IUsuarioBaseRepository según tu estructura final
    private final IUsuarioDatosEmpresaRepository datosEmpresaRepository;

    // ===========================================
    // ⬇️ Métodos heredados de IUsuarioBaseService ⬇️
    // ===========================================

    @Override
    public Usuario save(Usuario usuario) {
        // Delega la operación de salvar la entidad base Usuario
        return graduadoRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return graduadoRepository.findById(id);
    }

    @Override
    public List<Usuario> findAll() {
        // OJO: Esto trae todos los usuarios, sean graduados o anunciantes.
        return graduadoRepository.findAll();
    }
    @Override
    public void deleteById(Long id) {
        // NOTA: La eliminación de Usuario es compleja por las cascadas (Graduado/Empresa)
        // Por ahora, solo delegamos la eliminación de la entidad base.
        graduadoRepository.deleteById(id);
    }

    // ===========================================
    // ⬆️ Métodos heredados de IUsuarioBaseService ⬆️
    // ===========================================

    @Transactional
    @Override
    public void saveDatosEmpresa(Long usuarioId, UsuarioDatosEmpresa emp) {
        // 1. Verificar la existencia del usuario y obtener la entidad Usuario
        Usuario usuario = graduadoRepository.findById(usuarioId).orElseThrow(
                () -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId)
        );

        // 2. Buscar si ya existe una entidad de Datos Empresa para este usuario.
        // Se asume que IUsuarioDatosEmpresaRepository tiene el método findByUsuario_Id.
        UsuarioDatosEmpresa existingEmp = datosEmpresaRepository.findByUsuario_Id(usuarioId)
                .orElse(new UsuarioDatosEmpresa()); // Si no existe, crea una nueva

        // 3. Copiar los datos del DTO/entidad temporal al objeto persistente 'existingEmp'
        // Esto es crucial para manejar actualizaciones
        existingEmp.setRazonSocial(emp.getRazonSocial());
        existingEmp.setDireccion(emp.getDireccion());
        existingEmp.setCuit(emp.getCuit());
        existingEmp.setImagen(emp.getImagen());
        existingEmp.setEmail(emp.getEmail());
        existingEmp.setTelefono(emp.getTelefono());

        // 4. 🚨 ASIGNAR LA ENTIDAD USUARIO COMPLETA (Requerido por la relación @OneToOne)
        existingEmp.setUsuario(usuario);

        // 5. Guardar la entidad de empresa
        datosEmpresaRepository.save(existingEmp);
    }
}

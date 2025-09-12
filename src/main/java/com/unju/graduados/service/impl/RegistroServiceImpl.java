package com.unju.graduados.service.impl;

import com.unju.graduados.dto.RegistroCredencialesDTO;
import com.unju.graduados.model.Perfil;
import com.unju.graduados.model.Usuario;
import com.unju.graduados.model.UsuarioDatosAcademicos;
import com.unju.graduados.model.UsuarioDatosEmpresa;
import com.unju.graduados.model.UsuarioLogin;
import com.unju.graduados.model.dao.interfaces.IPerfilDao;
import com.unju.graduados.model.dao.interfaces.IUsuarioDao;
import com.unju.graduados.model.dao.interfaces.IUsuarioLoginDao;
import com.unju.graduados.service.IEmailService;
import com.unju.graduados.service.IRegistroService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistroServiceImpl implements IRegistroService {

    private final IUsuarioLoginDao usuarioLoginDao;
    private final IUsuarioDao usuarioDao;
    private final IPerfilDao perfilDao;
    private final PasswordEncoder passwordEncoder;
    private final IEmailService emailService;

    @Override
    @Transactional
    public void registrarNuevoUsuario(com.unju.graduados.dto.RegistroDTO dto) {
        throw new UnsupportedOperationException("Usar flujo por etapas");
    }

    @Override
    @Transactional
    public String registrarCredenciales(RegistroCredencialesDTO dto) {
        // Validaciones básicas
        if (!dto.passwordsMatch()) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        usuarioLoginDao.findByUsuario(dto.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Ya existe una cuenta registrada con ese email");
        });

        // Crear UsuarioLogin deshabilitado con token
        String token = UUID.randomUUID().toString();
        UsuarioLogin login = UsuarioLogin.builder()
                .usuario(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .habilitado(false)
                .registroCompleto(false)
                .codigoVerificacion(token)
                .fechaRegistro(ZonedDateTime.now())
                .build();
        usuarioLoginDao.save(login);
        emailService.sendVerificationEmail(dto.getEmail(), token);
        return token;
    }

    @Transactional
    @Override
    public Optional<UsuarioLogin> verificarToken(String token) {
        Optional<UsuarioLogin> opt = usuarioLoginDao.findByCodigoVerificacion(token);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        UsuarioLogin login = opt.get();
        // Expiración de token: 24 horas desde la fecha de registro
        ZonedDateTime registro = login.getFechaRegistro();
        if (registro == null || registro.plusHours(24).isBefore(ZonedDateTime.now())) {
            // Token expirado: limpiar y devolver vacío
            login.setCodigoVerificacion(null);
            usuarioLoginDao.save(login);
            return Optional.empty();
        }
        // Habilitar cuenta y limpiar token
        login.setHabilitado(true);
        login.setCodigoVerificacion(null);
        usuarioLoginDao.save(login);
        return Optional.of(login);
    }

    @Transactional
    @Override
    public Usuario completarDatosPersonales(Long loginId, Usuario datos, boolean esEgresado) {
        UsuarioLogin login = usuarioLoginDao.findById(loginId).orElseThrow();
        // Crear o actualizar Usuario asociado
        Usuario usuario = Optional.ofNullable(login.getIdUsuario())
                .flatMap(usuarioDao::findById)
                .orElse(Usuario.builder().build());
        usuario.setApellido(datos.getApellido());
        usuario.setNombre(datos.getNombre());
        usuario.setDni(datos.getDni());
        usuario.setEmail(login.getUsuario());
        usuario.setFechaNacimiento(datos.getFechaNacimiento());
        usuario.setTelefono(datos.getTelefono());
        usuario.setCelular(datos.getCelular());
        usuario = usuarioDao.save(usuario);
        // Vincular en login
        login.setIdUsuario(usuario.getId());
        usuarioLoginDao.save(login);
        return usuario;
    }

    @Transactional
    @Override
    public void asignarPerfilPorTipo(Long loginId, boolean esEgresado) {
        UsuarioLogin login = usuarioLoginDao.findById(loginId).orElseThrow();
        Long perfilId = esEgresado ? 4L : 1L; // GRADUADO:4, USUARIO:1
        Perfil perfil = perfilDao.findById(perfilId).orElseThrow();
        login.getPerfiles().add(perfil);
        usuarioLoginDao.save(login);
    }

    @Transactional
    @Override
    public void guardarDatosAcademicos(Long usuarioId, UsuarioDatosAcademicos acad) {
        Usuario usuario = usuarioDao.findById(usuarioId).orElseThrow();
        acad.setUsuario(usuario);
        usuario.setDatosAcademicos(acad);
        usuarioDao.save(usuario);
    }

    @Transactional
    @Override
    public void guardarDatosEmpresa(Long usuarioId, UsuarioDatosEmpresa emp) {
        Usuario usuario = usuarioDao.findById(usuarioId).orElseThrow();
        emp.setUsuario(usuario);
        usuario.setDatosEmpresa(emp);
        usuarioDao.save(usuario);
    }

    @Transactional
    @Override
    public void asignarPerfilesGraduadoYUsuario(Long loginId) {
        UsuarioLogin login = usuarioLoginDao.findById(loginId).orElseThrow();
        Perfil pUser = perfilDao.findById(1L).orElseThrow();
        Perfil pGrad = perfilDao.findById(4L).orElseThrow();
        login.getPerfiles().addAll(Set.of(pUser, pGrad));
        usuarioLoginDao.save(login);
    }

    @Override
    public void validarLoginUsuario(Long loginId, Long usuarioId) {
        UsuarioLogin login = usuarioLoginDao.findById(loginId).orElseThrow();
        if (login.getIdUsuario() == null || !login.getIdUsuario().equals(usuarioId)) {
            throw new IllegalArgumentException("El flujo de registro es inválido o está fuera de orden");
        }
    }
}

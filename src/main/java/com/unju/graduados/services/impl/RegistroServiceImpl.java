package com.unju.graduados.services.impl;

import com.unju.graduados.dto.RegistroCredencialesDTO;
import com.unju.graduados.dto.RegistroDTO;
import com.unju.graduados.dto.UsuarioDatosAcademicosDTO;
import com.unju.graduados.model.*;
import com.unju.graduados.model.repositories.*;
import com.unju.graduados.services.IEmailService;
import com.unju.graduados.services.IProvinciaService;
import com.unju.graduados.services.IRegistroService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistroServiceImpl implements IRegistroService {

    private final IUsuarioLoginRepository usuarioLoginDao;
    private final IUsuarioRepository usuarioDao;
    private final IPerfilRepository perfilDao;
    private final PasswordEncoder passwordEncoder;
    private final IEmailService emailService;
    private final IFacultadRepository facultadDao;
    private final ICarreraRepository carreraDao;
    private final IUniversidadRepository universidadDao;
    private final IProvinciaRepository provinciaDao;
    private final ILocalidadRepository localidadDao;
    private final IProvinciaService provinciaService;
    private final IColacionRepository colacionDao;

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
    public Usuario completarDatosPersonales(Long loginId, RegistroDTO dto, boolean esEgresado) {
        UsuarioLogin login = usuarioLoginDao.findById(loginId).orElseThrow();

        // Crear o actualizar Usuario asociado
        Usuario usuario = Optional.ofNullable(login.getIdUsuario())
                .flatMap(usuarioDao::findById)
                .orElse(Usuario.builder().build());

        // Datos personales
        usuario.setApellido(dto.getApellido());
        usuario.setNombre(dto.getNombre());
        usuario.setDni(dto.getDni());
        usuario.setEmail(login.getUsuario());

        // fechaNacimiento (guardamos solo si viene)
        if (dto.getFechaNacimiento() != null) {
            usuario.setFechaNacimiento(dto.getFechaNacimiento().atStartOfDay(ZoneId.systemDefault()));
        }

        usuario.setTelefono(dto.getTelefono());
        usuario.setCelular(dto.getCelular());

        // Procesar avatar solo si viene un archivo; si no viene, preservamos la imagen actual
        if (dto.getAvatar() != null && !dto.getAvatar().isEmpty()) {
            try {
                usuario.setImagen(dto.getAvatar().getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Error leyendo avatar", e);
            }
        }

        // Dirección
        UsuarioDireccion usuarioDireccion = usuario.getDireccion();
        if (usuarioDireccion == null) {
            usuarioDireccion = new UsuarioDireccion();
            usuarioDireccion.setUsuario(usuario);
        }

        if (dto.getProvinciaId() != null) {
            usuarioDireccion.setProvincia(provinciaService.findById(dto.getProvinciaId()));
        }

        usuarioDireccion.setDomicilio(dto.getDomicilio());

        // Localidad (crear si no existe)
        if (dto.getLocalidad() != null && !dto.getLocalidad().isBlank()) {
            Localidad localidad = localidadDao.findByNombre(dto.getLocalidad())
                    .orElseGet(() -> {
                        Localidad nueva = new Localidad();
                        nueva.setNombre(dto.getLocalidad());
                        return localidadDao.save(nueva);
                    });
            usuarioDireccion.setLocalidad(localidad);
        }

        usuario.setDireccion(usuarioDireccion);

        /*// Datos académicos que no van aqui.. deje el codigo en la asus

        usuario.setDatosAcademicos(datosAcademicos);*/

        usuario = usuarioDao.save(usuario);

        // Vincular login (si aún no estaba vinculado)
        login.setIdUsuario(usuario.getId());
        usuarioLoginDao.save(login);

        return usuario;
    }


    private Long parseLongOrNull(String value) {
        try {
            return (value != null && !value.isBlank()) ? Long.parseLong(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
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
    public void guardarDatosAcademicos(Long usuarioId, UsuarioDatosAcademicosDTO dto) {
        Usuario usuario = usuarioDao.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        UsuarioDatosAcademicos acad = usuario.getDatosAcademicos();
        if (acad == null) {
            acad = new UsuarioDatosAcademicos();
            acad.setUsuario(usuario);
        }

        // Resolver relaciones por ID
        if (dto.getIdUniversidad() != null) {
            Universidad uni = universidadDao.findById(dto.getIdUniversidad())
                    .orElseThrow(() -> new IllegalArgumentException("Universidad no encontrada"));
            acad.setUniversidad(uni);
        }

        if (dto.getIdFacultad() != null) {
            acad.setFacultad(facultadDao.findById(dto.getIdFacultad())
                    .orElseThrow(() -> new IllegalArgumentException("Facultad no encontrada")));
        }
        if (dto.getIdCarrera() != null) {
            acad.setCarrera(carreraDao.findById(dto.getIdCarrera()).orElse(null));
        }

        // Mapear campos simples
        acad.setMatricula(dto.getMatricula());
        acad.setIntereses(dto.getIntereses());
        acad.setEspecializaciones(dto.getEspecializaciones());
        acad.setIdiomas(dto.getIdiomas());
        acad.setPosgrado(dto.getPosgrado());
        acad.setTituloVerificado(Boolean.TRUE.equals(dto.getTituloVerificado()));

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

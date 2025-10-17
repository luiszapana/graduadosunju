package com.unju.graduados.services.impl;

import com.unju.graduados.dto.*;
import com.unju.graduados.model.*;
import com.unju.graduados.repositories.*;
import com.unju.graduados.services.IEmailService;
import com.unju.graduados.services.IProvinciaService;
import com.unju.graduados.services.IRegistroExternoService;
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
public class RegistroExternoServiceImpl implements IRegistroExternoService {

    private final IPerfilRepository perfilRepository;
    private final IUsuarioLoginRepository usuarioLoginRepository;
    private final IGraduadoRepository graduadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final IEmailService emailService;
    private final IFacultadRepository facultadRepository;
    private final ICarreraRepository carreraRepository;
    private final IUniversidadRepository universidadRepository;
    private final ILocalidadRepository localidadRepository;
    private final IProvinciaService provinciaService;
    private final IUsuarioDireccionRepository usuarioDireccionRepository;
    private final IUsuarioDatosAcademicosRepository datosAcademicosRepository;

    @Override
    @Transactional
    public void registrarCredenciales(RegistroCredencialesDTO dto) {
        if (!dto.passwordsMatch()) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        usuarioLoginRepository.findByUsuario(dto.getEmail())
                              .ifPresent(u -> {
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
        usuarioLoginRepository.save(login);
        emailService.sendVerificationEmail(dto.getEmail(), token);
        //return token;
    }

    @Transactional
    @Override
    public Optional<UsuarioLogin> verificarToken(String token) {
        Optional<UsuarioLogin> opt = usuarioLoginRepository.findByCodigoVerificacion(token);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        UsuarioLogin login = opt.get();
        // Expiración de token: 24 horas desde la fecha de registro
        ZonedDateTime registro = login.getFechaRegistro();
        if (registro == null || registro.plusHours(24).isBefore(ZonedDateTime.now())) {
            // Token expirado: limpiar y devolver vacío
            login.setCodigoVerificacion(null);
            usuarioLoginRepository.save(login);
            return Optional.empty();
        }
        // Habilitar cuenta y limpiar token
        login.setHabilitado(true);
        login.setCodigoVerificacion(null);
        usuarioLoginRepository.save(login);
        return Optional.of(login);
    }

    @Transactional
    @Override
    public Usuario completarDatosPersonales(Long loginId, RegistroDTO dto, boolean esEgresado) {
        UsuarioLogin login = usuarioLoginRepository.findById(loginId).orElseThrow();

        // 1. Crear o actualizar Usuario asociado (El Usuario se guarda al final para obtener el ID)
        Usuario usuario = Optional.ofNullable(login.getIdUsuario())
                .flatMap(graduadoRepository::findById)
                .orElse(Usuario.builder().build());
        // Datos personales
        usuario.setApellido(dto.getApellido());
        usuario.setNombre(dto.getNombre());
        usuario.setDni(dto.getDni());
        usuario.setEmail(login.getUsuario());

        // fechaNacimiento
        if (dto.getFechaNacimiento() != null) {
            usuario.setFechaNacimiento(dto.getFechaNacimiento().atStartOfDay(ZoneId.systemDefault()));
        }
        usuario.setTelefono(dto.getTelefono());
        usuario.setCelular(dto.getCelular());
        // Procesar avatar
        if (dto.getAvatar() != null && !dto.getAvatar().isEmpty()) {
            try {
                usuario.setImagen(dto.getAvatar().getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Error leyendo avatar", e);
            }
        }

        usuario = graduadoRepository.save(usuario);
        Long usuarioId = usuario.getId();

        // 2. Dirección
        UsuarioDireccion usuarioDireccion = usuarioDireccionRepository.findByIdUsuario(usuarioId)
                                                                      .orElse(new UsuarioDireccion());
        usuarioDireccion.setIdUsuario(usuarioId);
        if (dto.getProvinciaId() != null) {
            usuarioDireccion.setProvincia(provinciaService.findById(dto.getProvinciaId()));
        } else {
            usuarioDireccion.setProvincia(null);
        }
        usuarioDireccion.setDomicilio(dto.getDomicilio());

        // Localidad (crear si no existe)
        Localidad localidad = obtenerOCrearLocalidad(dto.getLocalidad());
        usuarioDireccion.setLocalidad(localidad);

        // Guardar la dirección
        usuarioDireccionRepository.save(usuarioDireccion);

        // 3. Vincular login
        login.setIdUsuario(usuarioId);
        usuarioLoginRepository.save(login);
        return usuario;
    }

    @Transactional
    @Override
    public void asignarPerfilPorTipo(Long loginId, boolean esEgresado) {
        UsuarioLogin login = usuarioLoginRepository.findById(loginId).orElseThrow();
        Long perfilId = esEgresado ? 4L : 1L; // GRADUADO:4, USUARIO:1
        Perfil perfil = perfilRepository.findById(perfilId).orElseThrow();
        login.getPerfiles().add(perfil);
        usuarioLoginRepository.save(login);
    }

    @Transactional
    @Override
    public void guardarDatosAcademicos(Long usuarioId, UsuarioDatosAcademicosDTO dto) {
        // No necesitamos cargar el Usuario completo, solo verificar si existe
        if (!graduadoRepository.existsById(usuarioId)) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Usamos el nuevo método de búsqueda por ID simple
        UsuarioDatosAcademicos acad = datosAcademicosRepository.findByIdUsuario(usuarioId)
                                                               .orElse(new UsuarioDatosAcademicos());
        // Usar el ID simple en lugar de setUsuario(usuario)
        acad.setIdUsuario(usuarioId);
        // Resolver relaciones por ID
        if (dto.getIdUniversidad() != null) {
            Universidad uni = universidadRepository.findById(dto.getIdUniversidad())
                                                   .orElseThrow(() -> new IllegalArgumentException("Universidad no encontrada"));
            acad.setUniversidad(uni);
        } else {
            acad.setUniversidad(null);
        }
        if (dto.getIdFacultad() != null) {
            acad.setFacultad(facultadRepository.findById(dto.getIdFacultad())
                    .orElseThrow(() -> new IllegalArgumentException("Facultad no encontrada")));
        } else {
            acad.setFacultad(null);
        }
        if (dto.getIdCarrera() != null) {
            acad.setCarrera(carreraRepository.findById(dto.getIdCarrera()).orElse(null));
        } else {
            acad.setCarrera(null);
        }
        // Mapear campos simples
        acad.setMatricula(dto.getMatricula());
        acad.setIntereses(dto.getIntereses());
        acad.setEspecializaciones(dto.getEspecializaciones());
        acad.setIdiomas(dto.getIdiomas());
        acad.setPosgrado(dto.getPosgrado());
        acad.setTituloVerificado(Boolean.TRUE.equals(dto.getTituloVerificado()));

        datosAcademicosRepository.save(acad);
    }

    @Transactional
    @Override
    public void asignarPerfilesGraduadoYUsuario(Long loginId) {
        UsuarioLogin login = usuarioLoginRepository.findById(loginId).orElseThrow();
        Perfil pUser = perfilRepository.findById(1L).orElseThrow();
        Perfil pGrad = perfilRepository.findById(4L).orElseThrow();
        login.getPerfiles().addAll(Set.of(pUser, pGrad));
        usuarioLoginRepository.save(login);
    }

    @Override
    public void validarLoginUsuario(Long loginId, Long usuarioId) {
        UsuarioLogin login = usuarioLoginRepository.findById(loginId).orElseThrow();
        if (login.getIdUsuario() == null || !login.getIdUsuario().equals(usuarioId)) {
            throw new IllegalArgumentException("El flujo de registro es inválido o está fuera de orden");
        }
    }

    /**
     * Busca una Localidad por nombre. Si no existe, la crea y la guarda.
     * Retorna null si el nombre de la localidad es nulo o vacío/en blanco.
     * @param nombreLocalidad Nombre de la localidad a buscar o crear.
     * @return La entidad Localidad, o null.
     */
    private Localidad obtenerOCrearLocalidad(String nombreLocalidad) {
        if (nombreLocalidad == null || nombreLocalidad.isBlank()) {
            return null;
        }
        // Buscar por nombre, si no existe, usar orElseGet para crearla y guardarla
        return localidadRepository.findByNombre(nombreLocalidad)
                .orElseGet(() -> {
                    Localidad nueva = new Localidad();
                    nueva.setNombre(nombreLocalidad);
                    // Guardar la nueva localidad en la base de datos
                    return localidadRepository.save(nueva);
                });
    }
}

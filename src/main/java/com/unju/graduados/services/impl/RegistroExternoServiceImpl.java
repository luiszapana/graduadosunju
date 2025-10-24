package com.unju.graduados.services.impl;

import com.unju.graduados.dto.*;
import com.unju.graduados.exceptions.DuplicatedResourceException;
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
import java.time.temporal.ChronoUnit;
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

        // 1. Normalizar y limpiar el email
        String emailNormalizado = dto.getEmail().trim().toLowerCase();

        // 2. VALIDACIÓN 1: Verificar si el email ya existe en la tabla Usuario (activos)
        if (graduadoRepository.existsByEmail(emailNormalizado)) {
            throw new DuplicatedResourceException(
                    "email", // Nombre del campo en el DTO
                    "Ya existe un usuario activo registrado con ese email."
            );
        }

        // 3. VALIDACIÓN 2 Y LIMPIEZA: Verificar y limpiar si el email existe en la tabla UsuarioLogin (pendientes)
        usuarioLoginRepository.findByUsuario(emailNormalizado)
                .ifPresent(loginExistente -> {

                    // Si el registro EXISTE pero no tiene ID de usuario (es un registro de alta externa incompleta)
                    if (loginExistente.getIdUsuario() == null) {

                        // --- LÓGICA DE LIMPIEZA POR EXPIRACIÓN ---
                        // Definir el umbral de expiración (ejemplo: 24 horas)
                        ZonedDateTime tiempoLimite = ZonedDateTime.now().minus(24, ChronoUnit.HOURS);

                        // Si el registro es antiguo (anterior al tiempo límite), lo eliminamos.
                        if (loginExistente.getFechaRegistro() != null && loginExistente.getFechaRegistro().isBefore(tiempoLimite)) {
                            // Se elimina el registro huérfano y obsoleto
                            usuarioLoginRepository.delete(loginExistente);

                            // El 'return' sale del ifPresent y permite que la ejecución continúe abajo
                            return;
                        }
                        // Si el registro EXISTE y NO ha expirado, lanzamos el error.
                        throw new DuplicatedResourceException(
                                "email", // Nombre del campo en el DTO
                                "Ya existe un registro pendiente de verificación para este email. Por favor, revise su correo o intente de nuevo más tarde."
                        );
                    }

                    // Si el registro EXISTE Y tiene id_usuario (Nunca debería pasar si el paso 2 es correcto,
                    // pero se lanza la excepción por seguridad)
                    throw new DuplicatedResourceException(
                            "email",
                            "Ya existe un registro de login asociado a un usuario activo."
                    );
                });

        // Si pasa las validaciones o el registro pendiente fue limpiado, procede con el nuevo registro
        String token = UUID.randomUUID().toString();
        UsuarioLogin login = UsuarioLogin.builder()
                .usuario(emailNormalizado) // Usar el email limpio aquí
                .password(passwordEncoder.encode(dto.getPassword()))
                .habilitado(false)
                .registroCompleto(false)
                .codigoVerificacion(token)
                .fechaRegistro(ZonedDateTime.now())
                .build();
        usuarioLoginRepository.save(login);
        emailService.sendVerificationEmail(dto.getEmail(), token);
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
        if (graduadoRepository.existsByDni(dto.getDni())) {
            throw new DuplicatedResourceException(
                    "dni", // Nombre del campo en el DTO
                    "El DNI '" + dto.getDni() + "' ya está registrado en el sistema."
            );
        }

        UsuarioLogin login = usuarioLoginRepository.findById(loginId).orElseThrow();

        // 1. Crear o actualizar Usuario asociado (El Usuario se guarda al final para obtener el ID)
        Usuario usuario = Optional.ofNullable(login.getIdUsuario())
                .flatMap(graduadoRepository::findById)
                .orElse(Usuario.builder().build());

        // Datos personales
        usuario.setApellido(dto.getApellido());
        usuario.setNombre(dto.getNombre());
        usuario.setDni(dto.getDni());
        usuario.setEmail(login.getUsuario()); // El email viene del login

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

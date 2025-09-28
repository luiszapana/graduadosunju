package com.unju.graduados.services.impl;

import com.unju.graduados.dto.AltaGraduadoAdminDTO;
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

    // Repositorios para UsuarioLogin y Perfil
    private final IUsuarioLoginRepository loginRepository; // Repositorio de UsuarioLogin (soluciona 'loginDao')
    private final IPerfilRepository perfilRepository;     // Repositorio de Perfil (para asignar GRADUADO)

    private final IUsuarioLoginRepository usuarioLoginRepository;
    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final IEmailService emailService;
    private final IFacultadRepository facultadRepository;
    private final ICarreraRepository carreraRepository;
    private final IUniversidadRepository universidadRepository;
    private final ILocalidadRepository localidadRepository;
    private final IProvinciaService provinciaService;
    private final IProvinciaRepository provinciaRepository;

    // Repositorios que FALTAN o que cambiaron de nombre:
    private final IUsuarioDatosAcademicosRepository academicosRepository; // ASUMO este nombre
    private final IColacionRepository colacionRepository; // ASUMO este nombre
    private final IUsuarioDireccionRepository usuarioDireccionRepository; // Necesario para guardar la Dirección

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
        usuarioLoginRepository.findByUsuario(dto.getEmail()).ifPresent(u -> {
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
        return token;
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

        // Crear o actualizar Usuario asociado
        Usuario usuario = Optional.ofNullable(login.getIdUsuario())
                .flatMap(usuarioRepository::findById)
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
            Localidad localidad = localidadRepository.findByNombre(dto.getLocalidad())
                    .orElseGet(() -> {
                        Localidad nueva = new Localidad();
                        nueva.setNombre(dto.getLocalidad());
                        return localidadRepository.save(nueva);
                    });
            usuarioDireccion.setLocalidad(localidad);
        }

        usuario.setDireccion(usuarioDireccion);

        /*// Datos académicos que no van aqui.. deje el codigo en la asus

        usuario.setDatosAcademicos(datosAcademicos);*/

        usuario = usuarioRepository.save(usuario);

        // Vincular login (si aún no estaba vinculado)
        login.setIdUsuario(usuario.getId());
        usuarioLoginRepository.save(login);

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
        UsuarioLogin login = usuarioLoginRepository.findById(loginId).orElseThrow();
        Long perfilId = esEgresado ? 4L : 1L; // GRADUADO:4, USUARIO:1
        Perfil perfil = perfilRepository.findById(perfilId).orElseThrow();
        login.getPerfiles().add(perfil);
        usuarioLoginRepository.save(login);
    }

    @Transactional
    @Override
    public void guardarDatosAcademicos(Long usuarioId, UsuarioDatosAcademicosDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        UsuarioDatosAcademicos acad = usuario.getDatosAcademicos();
        if (acad == null) {
            acad = new UsuarioDatosAcademicos();
            acad.setUsuario(usuario);
        }

        // Resolver relaciones por ID
        if (dto.getIdUniversidad() != null) {
            Universidad uni = universidadRepository.findById(dto.getIdUniversidad())
                    .orElseThrow(() -> new IllegalArgumentException("Universidad no encontrada"));
            acad.setUniversidad(uni);
        }

        if (dto.getIdFacultad() != null) {
            acad.setFacultad(facultadRepository.findById(dto.getIdFacultad())
                    .orElseThrow(() -> new IllegalArgumentException("Facultad no encontrada")));
        }
        if (dto.getIdCarrera() != null) {
            acad.setCarrera(carreraRepository.findById(dto.getIdCarrera()).orElse(null));
        }

        // Mapear campos simples
        acad.setMatricula(dto.getMatricula());
        acad.setIntereses(dto.getIntereses());
        acad.setEspecializaciones(dto.getEspecializaciones());
        acad.setIdiomas(dto.getIdiomas());
        acad.setPosgrado(dto.getPosgrado());
        acad.setTituloVerificado(Boolean.TRUE.equals(dto.getTituloVerificado()));

        usuario.setDatosAcademicos(acad);
        usuarioRepository.save(usuario);
    }

    @Transactional
    @Override
    public void guardarDatosEmpresa(Long usuarioId, UsuarioDatosEmpresa emp) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        emp.setUsuario(usuario);
        usuario.setDatosEmpresa(emp);
        usuarioRepository.save(usuario);
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

    // PARA TRABAJAR CON EL ALTA INTERNA
    @Transactional
    @Override
    public void registrarAltaInternaGraduado(AltaGraduadoAdminDTO dto) {

        // 1. Crear y Guardar UsuarioLogin
        UsuarioLogin login = UsuarioLogin.builder()
                .usuario(dto.getEmail())
                .password(passwordEncoder.encode(String.valueOf(dto.getDni())))
                .habilitado(true)
                .registroCompleto(true)
                .fechaRegistro(ZonedDateTime.now())
                .build();

        UsuarioLogin savedLogin = loginRepository.save(login);

        // 2. Crear y Guardar Usuario (Sin vincular login aún)
        Usuario usuario = new Usuario();
        usuario.setDni(dto.getDni());
        usuario.setApellido(dto.getApellido());
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail()); // El email del Usuario

        // Manejo de fecha de nacimiento (Ya corregido)
        if (dto.getFechaNacimiento() != null) {
            ZonedDateTime zdt = dto.getFechaNacimiento()
                    .atStartOfDay(ZoneId.systemDefault());
            usuario.setFechaNacimiento(zdt);
        } else {
            usuario.setFechaNacimiento(null);
        }

        usuario.setTelefono(dto.getTelefono());
        usuario.setCelular(dto.getCelular());

        // Guardamos el usuario sin dirección ni académicos (por ahora)
        Usuario savedUsuario = usuarioRepository.save(usuario);

        // 3. Crear y Guardar UsuarioDireccion (Usando la lógica del alta externa)
        UsuarioDireccion direccion = new UsuarioDireccion();
        direccion.setUsuario(savedUsuario); // Vínculo FK al Usuario
        direccion.setDomicilio(dto.getDomicilio());

        // Asignación de provincia
        if (dto.getProvinciaId() != null) {
            Provincia provincia = provinciaService.findById(dto.getProvinciaId());
            direccion.setProvincia(provincia);
        }

        // Asignación de localidad (Crear si no existe, igual que en completarDatosPersonales)
        if (dto.getLocalidad() != null && !dto.getLocalidad().isBlank()) {
            Localidad localidad = localidadRepository.findByNombre(dto.getLocalidad())
                    .orElseGet(() -> {
                        Localidad nueva = new Localidad();
                        nueva.setNombre(dto.getLocalidad());
                        return localidadRepository.save(nueva);
                    });
            direccion.setLocalidad(localidad);
        }

        // Guardar la dirección y enlazarla al usuario
        usuarioDireccionRepository.save(direccion);
        savedUsuario.setDireccion(direccion); // Enlazar al objeto Usuario

        // 4. Crear y Guardar Datos Académicos
        UsuarioDatosAcademicos academicos = new UsuarioDatosAcademicos();
        academicos.setUsuario(savedUsuario);

        academicos.setUniversidad(universidadRepository.findById(dto.getIdUniversidad()).orElse(null));
        academicos.setFacultad(facultadRepository.findById(dto.getIdFacultad()).orElse(null));
        academicos.setCarrera(carreraRepository.findById(dto.getIdCarrera()).orElse(null));

        academicos.setColacion(dto.getIdColacion() != null
                ? colacionRepository.findById(dto.getIdColacion()).orElse(null) // FIX: colacionRepository
                : null);

        // Otros campos
        academicos.setMatricula(dto.getMatricula());
        academicos.setIntereses(dto.getIntereses());
        academicos.setEspecializaciones(dto.getEspecializaciones());
        academicos.setIdiomas(dto.getIdiomas());
        academicos.setPosgrado(dto.getPosgrado());

        academicosRepository.save(academicos); // FIX: academicosRepository
        savedUsuario.setDatosAcademicos(academicos); // Enlazar al objeto Usuario

        // 5. Vincular Login y Asignar Perfil (Finalización)
        // Lógica de vinculación del alta externa:
        savedLogin.setIdUsuario(savedUsuario.getId());

        // Asignación de perfil GRADUADO (ID=4)
        Perfil graduadoPerfil = perfilRepository.findById(4L)
                .orElseThrow(() -> new RuntimeException("Perfil 'GRADUADO' no encontrado."));
        savedLogin.getPerfiles().add(graduadoPerfil);

        // Se guarda el login final
        loginRepository.save(savedLogin);
        usuarioRepository.save(savedUsuario); // Guardar los enlaces finales a Dirección y Académicos
    }
}

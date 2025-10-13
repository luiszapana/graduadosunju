package com.unju.graduados.services.impl;

import com.unju.graduados.dto.*;
import com.unju.graduados.model.*;
import com.unju.graduados.repositories.*;
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
    private final IUsuarioLoginRepository loginRepository;
    private final IPerfilRepository perfilRepository;
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
    private final IUsuarioDatosAcademicosRepository academicosRepository;
    private final IColacionRepository colacionRepository;
    private final IUsuarioDireccionRepository usuarioDireccionRepository;
    private final IUsuarioDatosAcademicosRepository datosAcademicosRepository;
    private final IUsuarioDatosEmpresaRepository datosEmpresaRepository;

    @Override
    @Transactional
    public void registrarNuevoUsuario(RegistroDTO dto) {
        throw new UnsupportedOperationException("Usar flujo por etapas");
    }

    @Override
    @Transactional
    public String registrarCredenciales(RegistroCredencialesDTO dto) {
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

        // 1. Crear o actualizar Usuario asociado (El Usuario se guarda al final para obtener el ID)
        Usuario usuario = Optional.ofNullable(login.getIdUsuario())
                .flatMap(usuarioRepository::findById)
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

        // **IMPORTANTE**: Guardar el usuario AQUI para obtener su ID si es nuevo
        usuario = usuarioRepository.save(usuario);
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
        if (!usuarioRepository.existsById(usuarioId)) {
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
    public void guardarDatosEmpresa(Long usuarioId, UsuarioDatosEmpresa emp) {
        // 1. Verificar la existencia del usuario y obtener la entidad Usuario
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(
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

        // 2. Crear y Guardar Usuario
        Usuario usuario = new Usuario();
        usuario.setDni(dto.getDni());
        usuario.setApellido(dto.getApellido());
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());

        if (dto.getFechaNacimiento() != null) {
            ZonedDateTime zdt = dto.getFechaNacimiento()
                    .atStartOfDay(ZoneId.systemDefault());
            usuario.setFechaNacimiento(zdt);
        } else {
            usuario.setFechaNacimiento(null);
        }
        usuario.setTelefono(dto.getTelefono());
        usuario.setCelular(dto.getCelular());

        // Guardamos el usuario para obtener el ID
        Usuario savedUsuario = usuarioRepository.save(usuario);
        Long usuarioId = savedUsuario.getId();

        // 3. Crear y Guardar UsuarioDireccion
        UsuarioDireccion direccion = new UsuarioDireccion();
        direccion.setIdUsuario(usuarioId);
        direccion.setDomicilio(dto.getDomicilio());
        if (dto.getProvinciaId() != null) {
            Provincia provincia = provinciaService.findById(dto.getProvinciaId());
            direccion.setProvincia(provincia);
        }
        Localidad localidad = obtenerOCrearLocalidad(dto.getLocalidad());
        direccion.setLocalidad(localidad);

        usuarioDireccionRepository.save(direccion);

        // 4. Crear y Guardar Datos Académicos
        UsuarioDatosAcademicos academicos = new UsuarioDatosAcademicos();
        academicos.setIdUsuario(usuarioId);
        academicos.setUniversidad(universidadRepository.findById(dto.getIdUniversidad()).orElse(null));
        academicos.setFacultad(facultadRepository.findById(dto.getIdFacultad()).orElse(null));
        academicos.setCarrera(carreraRepository.findById(dto.getIdCarrera()).orElse(null));
        academicos.setColacion(dto.getIdColacion() != null
                ? colacionRepository.findById(dto.getIdColacion()).orElse(null)
                : null);
        academicos.setMatricula(dto.getMatricula());
        academicos.setIntereses(dto.getIntereses());
        academicos.setEspecializaciones(dto.getEspecializaciones());
        academicos.setIdiomas(dto.getIdiomas());
        academicos.setPosgrado(dto.getPosgrado());

        academicosRepository.save(academicos);
        // 5. Vincular Login y Asignar Perfil (Finalización)
        savedLogin.setIdUsuario(usuarioId);

        Perfil graduadoPerfil = perfilRepository.findById(4L)
                .orElseThrow(() -> new RuntimeException("Perfil 'GRADUADO' no encontrado."));
        savedLogin.getPerfiles().add(graduadoPerfil);

        loginRepository.save(savedLogin);
    }

    @Override
    public EditarGraduadoAdminDTO obtenerGraduadoParaEdicion(Long id) {
        // 1. Traer usuario usando el nuevo método de Proyección Pura (SIN IMAGEN)
        IUsuarioSinImagen usuarioProyeccion = usuarioRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // 2. Traer datos académicos
        // ⭐ CAMBIO CRÍTICO: Usamos orElse(null) para permitir que los datos académicos sean nulos.
        UsuarioDatosAcademicos datosAcad = datosAcademicosRepository.findByIdUsuario(id)
                .orElse(null);

        // 3. Traer dirección asociada (puede no existir)
        UsuarioDireccion direccion = usuarioDireccionRepository.findByIdUsuario(id)
                .orElse(null);

        // 4. Crear DTO y setear campos
        EditarGraduadoAdminDTO dto = new EditarGraduadoAdminDTO();

        // Mapeo de campos simples desde la Proyección (IUsuarioSinImagen)
        dto.setId(usuarioProyeccion.getId());
        dto.setNombre(usuarioProyeccion.getNombre());
        dto.setApellido(usuarioProyeccion.getApellido());
        dto.setEmail(usuarioProyeccion.getEmail());
        dto.setDni(usuarioProyeccion.getDni());
        dto.setTelefono(usuarioProyeccion.getTelefono());
        dto.setCelular(usuarioProyeccion.getCelular());

        // Mapeo de fecha de nacimiento (ZonedDateTime -> LocalDate)
        dto.setFechaNacimiento(usuarioProyeccion.getFechaNacimiento() != null
                ? usuarioProyeccion.getFechaNacimiento().toLocalDate()
                : null);

        // Mapeo de campos de dirección (Condicional)
        if (direccion != null) {
            dto.setDomicilio(direccion.getDomicilio());
            // Se asume que Provincia y Localidad sí se cargan correctamente con sus IDs
            dto.setProvinciaId(direccion.getProvincia() != null ? direccion.getProvincia().getId() : null);
            dto.setLocalidad(direccion.getLocalidad() != null ? direccion.getLocalidad().getNombre() : null);
        }

        // Mapeo de campos de Datos Académicos (Condicional)
        if (datosAcad != null) { // ⭐ Nuevo control de nulidad aquí
            // Se asume que Universidad, Facultad y Carrera son entidades que pueden
            // obtenerse si datosAcad NO es nulo. Agregamos control para evitar NPE
            // si las relaciones internas (lazy loading) no se inicializan correctamente.
            dto.setIdUniversidad(datosAcad.getUniversidad() != null ? datosAcad.getUniversidad().getId() : null);
            dto.setIdFacultad(datosAcad.getFacultad() != null ? datosAcad.getFacultad().getId() : null);
            dto.setIdCarrera(datosAcad.getCarrera() != null ? datosAcad.getCarrera().getId() : null);
            dto.setIdColacion(datosAcad.getColacion() != null ? datosAcad.getColacion().getId() : null);

            dto.setMatricula(datosAcad.getMatricula());
            dto.setIntereses(datosAcad.getIntereses());
            dto.setEspecializaciones(datosAcad.getEspecializaciones());
            dto.setIdiomas(datosAcad.getIdiomas());
            dto.setPosgrado(datosAcad.getPosgrado());
            dto.setTituloVerificado(datosAcad.getTituloVerificado());
        } else {
            // Opcionalmente, puedes establecer valores por defecto para evitar problemas en el DTO/Vista
            // dto.setIdUniversidad(null);
            // dto.setIdFacultad(null);
            // ...
        }

        return dto;
    }
    @Override
    @Transactional
    public void actualizarGraduado(Long id, EditarGraduadoAdminDTO dto) {
        // 1. Traer y actualizar Usuario (entidad completa, pero sin usar la imagen en la carga)
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Actualizar campos de Usuario
        usuario.setEmail(dto.getEmail());
        usuario.setDni(dto.getDni());
        usuario.setApellido(dto.getApellido());
        usuario.setNombre(dto.getNombre());
        if (dto.getFechaNacimiento() != null) {
            usuario.setFechaNacimiento(dto.getFechaNacimiento().atStartOfDay(ZoneId.systemDefault()));
        } else {
            usuario.setFechaNacimiento(null);
        }
        usuario.setTelefono(dto.getTelefono());
        usuario.setCelular(dto.getCelular());

        usuarioRepository.save(usuario); // Guardar cambios directos del usuario

        // 3. Actualizar o crear dirección
        // Usamos el nuevo método de búsqueda por ID simple
        UsuarioDireccion direccion = usuarioDireccionRepository.findByIdUsuario(id)
                .orElse(new UsuarioDireccion());

        // 🚨 VINCULO CORREGIDO: Usar el ID simple en lugar de setUsuario(usuario)
        direccion.setIdUsuario(id);

        direccion.setDomicilio(dto.getDomicilio());

        // Provincia
        if (dto.getProvinciaId() != null) {
            Provincia provincia = provinciaRepository.findById(dto.getProvinciaId())
                    .orElseThrow(() -> new RuntimeException("Provincia no encontrada"));
            direccion.setProvincia(provincia);
        } else {
            direccion.setProvincia(null);
        }

        // Localidad (crear si no existe o dejar null)
        if (dto.getLocalidad() != null && !dto.getLocalidad().isBlank()) {
            Localidad localidad = obtenerOCrearLocalidad(dto.getLocalidad());
            direccion.setLocalidad(localidad);
        } else {
            direccion.setLocalidad(null);
        }

        usuarioDireccionRepository.save(direccion);

        // 4. Actualizar datos académicos
        // Usamos el nuevo método de búsqueda por ID simple
        UsuarioDatosAcademicos datosAcad = datosAcademicosRepository.findByIdUsuario(id)
                .orElseThrow(() -> new RuntimeException("Datos académicos no encontrados"));
        datosAcad.setIdUsuario(id);
        datosAcad.setFacultad(facultadRepository.findById(dto.getIdFacultad())
                .orElseThrow(() -> new RuntimeException("Facultad no encontrada")));
        datosAcad.setCarrera(carreraRepository.findById(dto.getIdCarrera())
                .orElseThrow(() -> new RuntimeException("Carrera no encontrada")));
        datosAcad.setColacion(dto.getIdColacion() != null ?
                colacionRepository.findById(dto.getIdColacion())
                        .orElseThrow(() -> new RuntimeException("Colación no encontrada"))
                : null);
        datosAcad.setMatricula(dto.getMatricula());
        datosAcad.setIntereses(dto.getIntereses());
        datosAcad.setEspecializaciones(datosAcad.getEspecializaciones());
        datosAcad.setIdiomas(datosAcad.getIdiomas());
        datosAcad.setPosgrado(datosAcad.getPosgrado());
        datosAcad.setTituloVerificado(dto.getTituloVerificado());

        datosAcademicosRepository.save(datosAcad);
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

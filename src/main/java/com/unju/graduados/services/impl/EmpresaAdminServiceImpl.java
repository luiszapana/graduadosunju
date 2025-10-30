package com.unju.graduados.services.impl;

import com.unju.graduados.dto.AltaEmpresaAdminDTO;
import com.unju.graduados.dto.EditarEmpresaAdminDTO;
import com.unju.graduados.exceptions.DuplicatedResourceException;
import com.unju.graduados.model.*;
import com.unju.graduados.repositories.*;
import com.unju.graduados.repositories.projections.EmpresaInfoProjection;
import com.unju.graduados.repositories.projections.UsuarioSinImagenProjection;
import com.unju.graduados.services.IEmpresaAdminService;
import com.unju.graduados.services.IProvinciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmpresaAdminServiceImpl implements IEmpresaAdminService {

    private final IEmpresaRepository anuncianteRepository;
    private final IUsuarioLoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
    private final IUsuarioRepository usuarioRepository;
    private final IUsuarioDireccionRepository usuarioDireccionRepository;
    private final IUsuarioDatosEmpresaRepository usuarioDatosEmpresaRepository;
    private final IPerfilRepository perfilRepository;
    private final IProvinciaRepository provinciaRepository;
    private final ILocalidadRepository localidadRepository;
    private final IProvinciaService provinciaService; // Usado en alta

    private final Long PERFIL_ANUNCIANTE_ID = 5L; // ID del perfil ANUNCIANTE

    // =========================================================================
    // MÉTODOS DE BÚSQUEDA Y LISTADO (AÑADIDOS PARA IMPLEMENTAR IAnuncianteAdminService)
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaInfoProjection> findByDniContaining(String dni, Pageable pageable) {
        return anuncianteRepository.findByDniContaining(dni, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaInfoProjection> findByEmailContainingIgnoreCase(String email, Pageable pageable) {
        return anuncianteRepository.findByEmailContainingIgnoreCase(email, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaInfoProjection> findByNombreContainingIgnoreCase(String nombre, Pageable pageable) {
        return anuncianteRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaInfoProjection> findByNombreEmpresaContainingIgnoreCase(String nombre, Pageable pageable) {
        return anuncianteRepository.findByRazonSocialContainingIgnoreCase(nombre, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaInfoProjection> findByApellidoContainingIgnoreCase(String apellido, Pageable pageable) {
        return anuncianteRepository.findByApellidoContainingIgnoreCase(apellido, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaInfoProjection> findByCuitContaining(String cuit, Pageable pageable) {
        return anuncianteRepository.findByCuitContaining(cuit, pageable);
    }

    // Método actualizado para coincidir con la interfaz, usando findByRazonSocialContainingIgnoreCase
    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaInfoProjection> findByRazonSocialContainingIgnoreCase(String razonSocial, Pageable pageable) {
        return anuncianteRepository.findByRazonSocialContainingIgnoreCase(razonSocial, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaInfoProjection> findAllAnunciantes(Pageable pageable) {
        return anuncianteRepository.findAllAnunciantes(pageable);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    // =========================================================================
    // ALTA INTERNA DE ANUNCIANTE (Métodos existentes)
    // =========================================================================
    @Transactional
    @Override
    public void registrarAltaInternaAnunciante(AltaEmpresaAdminDTO dto) {
        // 1. Validaciones
        if (anuncianteRepository.existsByDni(dto.getDni())) {
            throw new DuplicatedResourceException("dni", "El DNI '" + dto.getDni() + "' ya está registrado.");
        }
        if (anuncianteRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicatedResourceException("email", "El Email '" + dto.getEmail() + "' ya está registrado.");
        }
        if (usuarioDatosEmpresaRepository.existsByCuit(dto.getCuit())) {
            throw new DuplicatedResourceException("cuit", "El CUIT '" + dto.getCuit() + "' ya está registrado.");
        }

        // 2. Crear y Guardar UsuarioLogin
        UsuarioLogin login = crearUsuarioLogin(dto.getEmail(), dto.getDni());
        UsuarioLogin savedLogin = loginRepository.save(login);

        // 3. Crear y Guardar Usuario (Base)
        Usuario usuario = new Usuario();
        mapearUsuarioDesdeDTO(usuario, dto); // Mapeo de campos base
        Usuario savedUsuario = usuarioRepository.save(usuario);
        Long usuarioId = savedUsuario.getId();

        // 4. Crear y Guardar UsuarioDireccion
        UsuarioDireccion direccion = new UsuarioDireccion();
        mapearDireccionDesdeDTO(direccion, dto, usuarioId); // Mapeo de dirección
        usuarioDireccionRepository.save(direccion);

        // 5. Crear y Guardar Datos de Empresa
        UsuarioDatosEmpresa datosEmpresa = new UsuarioDatosEmpresa();
        datosEmpresa.setIdUsuario(usuarioId);
        datosEmpresa.setRazonSocial(dto.getRazonSocial());
        datosEmpresa.setCuit(dto.getCuit());
        datosEmpresa.setDireccion(dto.getDireccion());
        datosEmpresa.setEmail(dto.getEmailEmpresa());
        datosEmpresa.setTelefono(dto.getTelefonoEmpresa());
        if (dto.getImagenFile() != null && !dto.getImagenFile().isEmpty()) {
            try {
                // La Entidad espera byte[], lo obtenemos del MultipartFile
                datosEmpresa.setImagen(dto.getImagenFile().getBytes());
            } catch (IOException e) {
                log.error("Error al leer el archivo de imagen para el anunciante.", e);
                // Opcional: Lanzar una excepción de negocio si la lectura falla
                throw new RuntimeException("Error al procesar el archivo de imagen.", e);
            }
        } else {
            datosEmpresa.setImagen(null); // Asegurar que sea null si no hay archivo
        }
        // Eliminados: icono y link_red_social

        usuarioDatosEmpresaRepository.save(datosEmpresa);

        // 6. Vincular Login y Asignar Perfil
        savedLogin.setIdUsuario(usuarioId);
        Perfil anunciantePerfil = perfilRepository.findById(PERFIL_ANUNCIANTE_ID)
                .orElseThrow(() -> new RuntimeException("Perfil 'ANUNCIANTE' no encontrado."));
        savedLogin.setPerfiles(Collections.singleton(anunciantePerfil));

        loginRepository.save(savedLogin);
    }

    // =========================================================================
    // OBTENER ANUNCIANTE PARA EDICIÓN (Métodos existentes)
    // =========================================================================
    @Override
    public EditarEmpresaAdminDTO obtenerAnuncianteParaEdicion(Long id) {
        // 1. Traer proyecciones y datos
        UsuarioSinImagenProjection usuarioProyeccion = anuncianteRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("Anunciante (Usuario) no encontrado con ID: " + id));

        UsuarioDatosEmpresa datosEmpresa = usuarioDatosEmpresaRepository.findByIdUsuario(id)
                .orElseThrow(() -> new RuntimeException("Datos de empresa no encontrados para el anunciante ID: " + id));

        UsuarioDireccion direccion = usuarioDireccionRepository.findByIdUsuario(id)
                .orElse(null);

        // 2. Crear DTO y Mapear campos base
        EditarEmpresaAdminDTO dto = new EditarEmpresaAdminDTO();

        dto.setId(usuarioProyeccion.getId());
        dto.setNombre(usuarioProyeccion.getNombre());
        dto.setApellido(usuarioProyeccion.getApellido());
        dto.setEmail(usuarioProyeccion.getEmail());
        dto.setDni(usuarioProyeccion.getDni());
        dto.setTelefono(usuarioProyeccion.getTelefono());
        dto.setCelular(usuarioProyeccion.getCelular());

        dto.setFechaNacimiento(usuarioProyeccion.getFechaNacimiento() != null
                ? usuarioProyeccion.getFechaNacimiento().toLocalDate() : null);

        // 3. Mapeo de campos de dirección
        if (direccion != null) {
            dto.setDomicilio(direccion.getDomicilio());
            dto.setProvinciaId(direccion.getProvincia() != null ? direccion.getProvincia().getId() : null);
            dto.setLocalidad(direccion.getLocalidad() != null ? direccion.getLocalidad().getNombre() : null);
        }

        // 4. Mapeo de campos de Datos de Empresa
        dto.setRazonSocial(datosEmpresa.getRazonSocial());
        dto.setCuit(datosEmpresa.getCuit());
        dto.setDireccion(datosEmpresa.getDireccion());
        dto.setEmailEmpresa(datosEmpresa.getEmail());
        dto.setTelefonoEmpresa(datosEmpresa.getTelefono());
        dto.setImagenActual(datosEmpresa.getImagen());

        return dto;
    }

    // =========================================================================
    // ACTUALIZAR ANUNCIANTE (Métodos existentes)
    // =========================================================================
    @Override
    @Transactional
    public void actualizarAnunciante(Long id, EditarEmpresaAdminDTO dto) {
        // 1. Traer Usuario
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anunciante (Usuario) no encontrado"));

        // 2. Actualizar campos de Usuario (Base)
        mapearUsuarioDesdeDTO(usuario, dto);
        usuarioRepository.save(usuario);

        // 3. Actualizar o crear dirección
        UsuarioDireccion direccion = usuarioDireccionRepository.findByIdUsuario(id)
                .orElse(new UsuarioDireccion());
        mapearDireccionDesdeDTO(direccion, dto, id);
        usuarioDireccionRepository.save(direccion);

        // 4. Actualizar datos de empresa
        UsuarioDatosEmpresa datosEmpresa = usuarioDatosEmpresaRepository.findByIdUsuario(id)
                .orElseThrow(() -> new RuntimeException("Datos de empresa no encontrados"));

        datosEmpresa.setIdUsuario(id);
        datosEmpresa.setRazonSocial(dto.getRazonSocial());
        datosEmpresa.setCuit(dto.getCuit());
        datosEmpresa.setDireccion(dto.getDireccion());
        datosEmpresa.setEmail(dto.getEmailEmpresa());
        datosEmpresa.setTelefono(dto.getTelefonoEmpresa());
        if (dto.getImagenFileNueva() != null && !dto.getImagenFileNueva().isEmpty()) {
            try {
                datosEmpresa.setImagen(dto.getImagenFileNueva().getBytes());
            } catch (java.io.IOException e) {
                log.error("Error al leer el nuevo archivo de imagen para el anunciante ID {}.", id, e);
                throw new RuntimeException("Error al procesar el archivo de imagen.", e);
            }
        }
        usuarioDatosEmpresaRepository.save(datosEmpresa);
    }

    // =========================================================================
    // MÉTODOS AUXILIARES (Métodos existentes)
    // =========================================================================

    private UsuarioLogin crearUsuarioLogin(String email, String dni) {
        return UsuarioLogin.builder()
                .usuario(email)
                .password(passwordEncoder.encode(dni)) // DNI como password inicial
                .habilitado(true)
                .registroCompleto(true)
                .fechaRegistro(ZonedDateTime.now())
                .build();
    }

    // --- Mapeo de Usuario (Alta)
    private void mapearUsuarioDesdeDTO(Usuario usuario, AltaEmpresaAdminDTO dto) {
        usuario.setDni(dto.getDni());
        usuario.setApellido(dto.getApellido());
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setCelular(dto.getCelular());
        if (dto.getFechaNacimiento() != null) {
            usuario.setFechaNacimiento(dto.getFechaNacimiento().atStartOfDay(ZoneId.systemDefault()));
        } else {
            usuario.setFechaNacimiento(null);
        }
    }

    // --- Mapeo de Usuario (Edición)
    private void mapearUsuarioDesdeDTO(Usuario usuario, EditarEmpresaAdminDTO dto) {
        usuario.setDni(dto.getDni());
        usuario.setApellido(dto.getApellido());
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setCelular(dto.getCelular());
        if (dto.getFechaNacimiento() != null) {
            usuario.setFechaNacimiento(dto.getFechaNacimiento().atStartOfDay(ZoneId.systemDefault()));
        } else {
            usuario.setFechaNacimiento(null);
        }
    }

    // --- Mapeo de Dirección (Alta)
    private void mapearDireccionDesdeDTO(UsuarioDireccion direccion, AltaEmpresaAdminDTO dto, Long usuarioId) {
        direccion.setIdUsuario(usuarioId);
        direccion.setDomicilio(dto.getDomicilio());

        if (dto.getProvinciaId() != null) {
            Provincia provincia = provinciaService.findById(dto.getProvinciaId());
            direccion.setProvincia(provincia);
        }

        Localidad localidad = obtenerOCrearLocalidad(dto.getLocalidad());
        direccion.setLocalidad(localidad);
    }

    // --- Mapeo de Dirección (Edición)
    private void mapearDireccionDesdeDTO(UsuarioDireccion direccion, EditarEmpresaAdminDTO dto, Long usuarioId) {
        direccion.setIdUsuario(usuarioId);
        direccion.setDomicilio(dto.getDomicilio());

        // Provincia
        if (dto.getProvinciaId() != null) {
            Provincia provincia = provinciaRepository.findById(dto.getProvinciaId())
                    .orElseThrow(() -> new RuntimeException("Provincia no encontrada"));
            direccion.setProvincia(provincia);
        } else {
            direccion.setProvincia(null);
        }

        // Localidad
        if (dto.getLocalidad() != null && !dto.getLocalidad().isBlank()) {
            Localidad localidad = obtenerOCrearLocalidad(dto.getLocalidad());
            direccion.setLocalidad(localidad);
        } else {
            direccion.setLocalidad(null);
        }
    }

    /**
     * Busca una Localidad por nombre. Si no existe, la crea y la guarda.
     * @param nombreLocalidad Nombre de la localidad a buscar o crear.
     * @return La entidad Localidad, o null.
     */
    private Localidad obtenerOCrearLocalidad(String nombreLocalidad) {
        if (nombreLocalidad == null || nombreLocalidad.isBlank()) {
            return null;
        }
        return localidadRepository.findByNombre(nombreLocalidad)
                .orElseGet(() -> {
                    Localidad nueva = new Localidad();
                    nueva.setNombre(nombreLocalidad);
                    return localidadRepository.save(nueva);
                });
    }
}


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
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmpresaAdminServiceImpl implements IEmpresaAdminService {

    private final IEmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final IUsuarioRepository usuarioRepository;
    private final IUsuarioDireccionRepository usuarioDireccionRepository;
    private final IUsuarioDatosEmpresaRepository usuarioDatosEmpresaRepository;
    private final IPerfilRepository perfilRepository;
    private final IProvinciaRepository provinciaRepository;
    private final ILocalidadRepository localidadRepository;
    private final IProvinciaService provinciaService;
    private final IUsuarioLoginRepository usuarioLoginRepository;
    private final IUsuarioLoginPerfilesRepository usuarioLoginPerfilesRepository;

    private final Long PERFIL_ANUNCIANTE_ID = 5L; // ID del perfil ANUNCIANTE

    // =========================================================================
    // MÉTODOS DE BÚSQUEDA Y LISTADO (AÑADIDOS PARA IMPLEMENTAR IAnuncianteAdminService)
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaInfoProjection> findByDniContaining(String dni, Pageable pageable) {
        return empresaRepository.findByDniContaining(dni, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaInfoProjection> findByEmailContainingIgnoreCase(String email, Pageable pageable) {
        return empresaRepository.findByEmailContainingIgnoreCase(email, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaInfoProjection> findByNombreContainingIgnoreCase(String nombre, Pageable pageable) {
        return empresaRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaInfoProjection> findByNombreEmpresaContainingIgnoreCase(String nombre, Pageable pageable) {
        return empresaRepository.findByRazonSocialContainingIgnoreCase(nombre, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaInfoProjection> findByApellidoContainingIgnoreCase(String apellido, Pageable pageable) {
        return empresaRepository.findByApellidoContainingIgnoreCase(apellido, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaInfoProjection> findByCuitContaining(String cuit, Pageable pageable) {
        return empresaRepository.findByCuitContaining(cuit, pageable);
    }

    // Método actualizado para coincidir con la interfaz, usando findByRazonSocialContainingIgnoreCase
    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaInfoProjection> findByRazonSocialContainingIgnoreCase(String razonSocial, Pageable pageable) {
        return empresaRepository.findByRazonSocialContainingIgnoreCase(razonSocial, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaInfoProjection> findAllAnunciantes(Pageable pageable) {
        return empresaRepository.findAllAnunciantes(pageable);
    }

    @Override
    @Transactional
    public void deleteById(Long usuarioId) {
        // 0. El usuario principal DEBE existir para continuar
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Anunciante/Usuario no encontrado con ID: " + usuarioId));

        // 1. Eliminar Datos de Empresa (Tabla específica de anunciantes)
        usuarioDatosEmpresaRepository.findByIdUsuario(usuarioId)
                .ifPresent(usuarioDatosEmpresaRepository::delete);

        // 2. Eliminar Dirección (Si existe)
        usuarioDireccionRepository.findByIdUsuario(usuarioId)
                .ifPresent(usuarioDireccionRepository::delete);

        // 3. Eliminar Login y sus Perfiles (Si existe)
        usuarioLoginRepository.findByIdUsuario(usuarioId)
                .ifPresent(usuarioLogin -> {
                    // A. Eliminar perfiles asociados al login
                    Long loginId = usuarioLogin.getId();
                    usuarioLoginPerfilesRepository.deleteByLoginId(loginId);
                    // B. Eliminar login
                    usuarioLoginRepository.deleteById(loginId);
                });
        // 4. Finalmente eliminar usuario raíz
        usuarioRepository.delete(usuario);
    }

    // =========================================================================
    // ALTA INTERNA DE ANUNCIANTE (Métodos existentes)
    // =========================================================================
    @Transactional
    @Override
    public void registrarAltaInternaAnunciante(AltaEmpresaAdminDTO dto) {
        // 1. Validaciones
        if (empresaRepository.existsByDni(dto.getDni())) {
            throw new DuplicatedResourceException("dni", "El DNI '" + dto.getDni() + "' ya está registrado.");
        }
        if (empresaRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicatedResourceException("email", "El Email '" + dto.getEmail() + "' ya está registrado.");
        }
        if (usuarioDatosEmpresaRepository.existsByCuit(dto.getCuit())) {
            throw new DuplicatedResourceException("cuit", "El CUIT '" + dto.getCuit() + "' ya está registrado.");
        }

        // 2. Crear y Guardar UsuarioLogin
        UsuarioLogin login = crearUsuarioLogin(dto.getEmail(), dto.getDni());
        UsuarioLogin savedLogin = usuarioLoginRepository.save(login);

        // 3. Crear y Guardar Usuario (Base)
        Usuario usuario = new Usuario();

        // ⭐ Llama al auxiliar que ahora sí incluye el mapeo correcto de la fecha.
        mapearUsuarioDesdeDTO(usuario, dto);

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
                throw new RuntimeException("Error al procesar el archivo de imagen.", e);
            }
        } else {
            datosEmpresa.setImagen(null); // Asegurar que sea null si no hay archivo
        }
        usuarioDatosEmpresaRepository.save(datosEmpresa);

        // 6. Vincular Login y Asignar Perfil
        savedLogin.setIdUsuario(usuarioId);
        Perfil anunciantePerfil = perfilRepository.findById(PERFIL_ANUNCIANTE_ID)
                .orElseThrow(() -> new RuntimeException("Perfil 'ANUNCIANTE' no encontrado."));
        Set<Perfil> perfiles = new HashSet<>();
        perfiles.add(anunciantePerfil);
        savedLogin.setPerfiles(perfiles);

        usuarioLoginRepository.save(savedLogin);
    }

    // =========================================================================
    // OBTENER ANUNCIANTE PARA EDICIÓN
    // =========================================================================
    @Override
    public EditarEmpresaAdminDTO obtenerAnuncianteParaEdicion(Long id) {
        // 1. Traer proyecciones y datos
        UsuarioSinImagenProjection usuarioProyeccion = empresaRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("Anunciante (Usuario) no encontrado con ID: " + id));

        UsuarioDatosEmpresa datosEmpresa = usuarioDatosEmpresaRepository.findByIdUsuario(id)
                .orElseThrow(() -> new RuntimeException("Datos de empresa no encontrados para el anunciante ID: " + id));

        // Usamos findByIdUsuario, asumiendo que la relación es OneToOne/OneToMany y buscamos por FK
        UsuarioDireccion direccion = usuarioDireccionRepository.findByIdUsuario(id)
                .orElse(null); // Puede ser null si la dirección es opcional

        // 2. Crear DTO y Mapear IDs ocultos y campos base
        EditarEmpresaAdminDTO dto = new EditarEmpresaAdminDTO();

        // === Mapeo de IDs Ocultos (CRUCIAL para la edición) ===
        dto.setId(usuarioProyeccion.getId());
        // Asignar el ID de la entidad de Datos de Empresa
        dto.setIdUsuarioDatosEmpresa(datosEmpresa.getId());
        // Asignar el ID de la entidad de Dirección, si existe
        if (direccion != null) {
            dto.setIdUsuarioDireccion(direccion.getId());
        }

        // === Mapeo de campos de Usuario (Base) ===
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

        // === Mapeo de Imagen (Conversión a Base64) ===
        if (datosEmpresa.getImagen() != null && datosEmpresa.getImagen().length > 0) {
            // Convierte el array de bytes a Base64 para que el HTML lo pueda renderizar.
            dto.setImagenActualBase64(java.util.Base64.getEncoder().encodeToString(datosEmpresa.getImagen()));
            // Esto indica al HTML que hay una imagen que debe considerarse "actual" para la lógica de reemplazo.
            dto.setMantenerImagenActual(true);
        } else {
            dto.setMantenerImagenActual(false);
        }

        return dto;
    }

    // =========================================================================
// ACTUALIZAR ANUNCIANTE
// =========================================================================
    @Override
    @Transactional
    public void actualizarAnunciante(Long id, EditarEmpresaAdminDTO dto) {
        // === 1. Traer y Actualizar Usuario (Base) ===
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anunciante (Usuario) no encontrado"));

        // Asume que este método actualiza campos como nombre, apellido, email, etc.
        // **Nota:** Debes actualizar el login (email/contraseña) si es necesario.
        mapearUsuarioDesdeDTO(usuario, dto);
        // El save es implícito al final de @Transactional, pero lo mantenemos si quieres flushear antes.
        usuarioRepository.save(usuario);

        // === 2. Actualizar Dirección ===
        // CRUCIAL: Usamos el ID de la dirección (idUsuarioDireccion) para cargarla y actualizarla
        UsuarioDireccion direccion = usuarioDireccionRepository.findById(dto.getIdUsuarioDireccion())
                .orElse(new UsuarioDireccion());

        // Asume que este método actualiza domicilio, provincia, localidad y setea el idUsuario
        mapearDireccionDesdeDTO(direccion, dto, id);
        usuarioDireccionRepository.save(direccion);

        // === 3. Actualizar Datos de Empresa ===
        // CRUCIAL: Usamos el ID de la empresa (idUsuarioDatosEmpresa) para cargarla y actualizarla
        UsuarioDatosEmpresa datosEmpresa = usuarioDatosEmpresaRepository.findById(dto.getIdUsuarioDatosEmpresa())
                .orElseThrow(() -> new RuntimeException("Datos de empresa no encontrados para el ID: " + dto.getIdUsuarioDatosEmpresa()));

        // El ID de usuario ya debe estar seteado en la entidad, pero lo reforzamos por si es nuevo.
        datosEmpresa.setIdUsuario(id);
        datosEmpresa.setRazonSocial(dto.getRazonSocial());
        datosEmpresa.setCuit(dto.getCuit());
        datosEmpresa.setDireccion(dto.getDireccion());
        datosEmpresa.setEmail(dto.getEmailEmpresa());
        datosEmpresa.setTelefono(dto.getTelefonoEmpresa());

        // === Lógica de Actualización/Eliminación de Imagen ===
        if (dto.getImagenFile() != null && !dto.getImagenFile().isEmpty()) {
            // 3a. SUBIR NUEVA IMAGEN: Si se subió un archivo nuevo, lo guardamos.
            try {
                datosEmpresa.setImagen(dto.getImagenFile().getBytes());
            } catch (java.io.IOException e) {
                log.error("Error al leer el nuevo archivo de imagen para el anunciante ID {}.", id, e);
                throw new RuntimeException("Error al procesar el archivo de imagen.", e);
            }
        } else if (dto.getMantenerImagenActual() == null || dto.getMantenerImagenActual() == false) {
            // 3b. ELIMINAR IMAGEN: Si no se subió un archivo Y el flag de mantener NO está activo (o es nulo)
            // Esto asume que el HTML tiene una lógica para deshabilitar el flag si el usuario quiere eliminarla.
            // Si el DTO no trae el campo (porque es hidden y el browser lo omitió), asumimos que FALSE es la intención.
            datosEmpresa.setImagen(null);
        }
        // 3c. MANTENER IMAGEN: Si no se subió un archivo nuevo Y el flag de mantener es TRUE, no hacemos nada.

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
// Método auxiliar que mapea los campos del DTO de Alta a la Entidad Usuario.
    private void mapearUsuarioDesdeDTO(Usuario usuario, AltaEmpresaAdminDTO dto) {
        usuario.setDni(dto.getDni());
        usuario.setApellido(dto.getApellido());
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setCelular(dto.getCelular());

        // ⭐ Mapeo de Fecha: Conversión de LocalDate a ZonedDateTime
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


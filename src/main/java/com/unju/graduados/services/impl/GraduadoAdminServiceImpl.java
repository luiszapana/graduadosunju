package com.unju.graduados.services.impl;

import com.unju.graduados.dto.AltaGraduadoAdminDTO;
import com.unju.graduados.dto.EditarGraduadoAdminDTO;
import com.unju.graduados.exceptions.DuplicatedResourceException;
import com.unju.graduados.model.*;
import com.unju.graduados.repositories.*;
import com.unju.graduados.repositories.projections.UsuarioSinImagenProjection;
import com.unju.graduados.services.IGraduadoAdminService;
import com.unju.graduados.services.IProvinciaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class GraduadoAdminServiceImpl implements IGraduadoAdminService {

    private final IUsuarioLoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
    private final IGraduadoRepository graduadoRepository;
    private final IProvinciaService provinciaService;
    private final IUsuarioDireccionRepository usuarioDireccionRepository;
    private final IUniversidadRepository universidadRepository;
    private final IFacultadRepository facultadRepository;
    private final ICarreraRepository carreraRepository;
    private final IColacionRepository colacionRepository;
    private final IPerfilRepository perfilRepository;
    private final IUsuarioDatosAcademicosRepository usuarioDatosAcademicosRepository;
    private final IProvinciaRepository provinciaRepository;
    private final ILocalidadRepository localidadRepository;

    @Transactional
    @Override
    public void registrarAltaInternaGraduado(AltaGraduadoAdminDTO dto) {
        // 1. Validar DNI duplicado
        if (graduadoRepository.existsByDni(dto.getDni())) {
            throw new DuplicatedResourceException(
                    "dni",
                    "El DNI '" + dto.getDni() + "' ya está registrado."
            );
        }

        // 2. Validar Email duplicado
        if (graduadoRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicatedResourceException(
                    "email",
                    "El Email '" + dto.getEmail() + "' ya está registrado."
            );
        }

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
        Usuario savedUsuario = graduadoRepository.save(usuario);
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

        usuarioDatosAcademicosRepository.save(academicos);
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
        UsuarioSinImagenProjection usuarioProyeccion = graduadoRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // 2. Traer datos académicos
        // ⭐ CAMBIO CRÍTICO: Usamos orElse(null) para permitir que los datos académicos sean nulos.
        UsuarioDatosAcademicos datosAcad = usuarioDatosAcademicosRepository.findByIdUsuario(id)
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
        }
        return dto;
    }

    @Override
    @Transactional
    public void actualizarGraduado(Long id, EditarGraduadoAdminDTO dto) {
        // 1. Traer y actualizar Usuario (entidad completa, pero sin usar la imagen en la carga)
        Usuario usuario = graduadoRepository.findById(id)
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

        graduadoRepository.save(usuario); // Guardar cambios directos del usuario

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
        UsuarioDatosAcademicos datosAcad = usuarioDatosAcademicosRepository.findByIdUsuario(id)
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

        usuarioDatosAcademicosRepository.save(datosAcad);
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

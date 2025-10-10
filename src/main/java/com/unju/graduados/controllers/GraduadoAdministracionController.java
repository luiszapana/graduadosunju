package com.unju.graduados.controllers;

import com.unju.graduados.dto.AltaGraduadoAdminDTO;
import com.unju.graduados.dto.EditarGraduadoAdminDTO;
import com.unju.graduados.exceptions.DuplicatedResourceException;
import com.unju.graduados.model.Usuario;
import com.unju.graduados.repositories.IFacultadRepository;
import com.unju.graduados.repositories.IUsuarioInfo;
import com.unju.graduados.repositories.impl.UsuarioInfoImpl;
import com.unju.graduados.services.IColacionService;
import com.unju.graduados.services.IProvinciaService;
import com.unju.graduados.services.IRegistroService;
import com.unju.graduados.services.IUsuarioService;
import com.unju.graduados.util.PaginacionUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/graduados")
@PreAuthorize("hasAnyRole('MODERADOR','ADMINISTRADOR')")
@RequiredArgsConstructor
public class GraduadoAdministracionController {
    private final IUsuarioService usuarioService;
    private final IRegistroService registroService;
    private final IProvinciaService provinciaService;
    private final IFacultadRepository facultadDao;
    private final IColacionService colacionService;

    /**
     * Listado paginado de graduados (usuarios).
     */
    @GetMapping
    public String listarUsuarios(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size, Model model) {
        Page<IUsuarioInfo> usuariosPage = usuarioService.findAllGraduados(PageRequest.of(page, size));
        int pagesToShow = 5;
        List<Integer> pageNumbers = PaginacionUtil.calcularRangoPaginas(usuariosPage, pagesToShow);
        // 2. Agrega los atributos al modelo
        model.addAttribute("page", usuariosPage);
        model.addAttribute("usuarios", usuariosPage.getContent());
        model.addAttribute("pageNumbers", pageNumbers);
        model.addAttribute("totalRegistros", usuariosPage.getTotalElements());
        return "admin/graduados";
    }

    /**
     * Formulario de alta.
     */
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        if (!model.containsAttribute("altaGraduadoAdminDTO")) {
            model.addAttribute("altaGraduadoAdminDTO", new AltaGraduadoAdminDTO());
        }
        cargarDatosFormulario(model);
        return "admin/graduado-form";
    }

    /**
     * Procesar alta interna.
     */
    @PostMapping("/guardar")
    public String procesarAlta(
            @Valid @ModelAttribute("altaGraduadoAdminDTO") AltaGraduadoAdminDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarDatosFormulario(model);
            return "admin/graduado-form";
        }

        try {
            registroService.registrarAltaInternaGraduado(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Graduado registrado con éxito.");
            return "redirect:/admin/graduados";

        } catch (DuplicatedResourceException e) {
            result.rejectValue(e.getFieldName(), "duplicated", e.getMessage());
            cargarDatosFormulario(model);
            return "admin/graduado-form";

        } catch (RuntimeException e) {
            result.reject("unexpected", "Error inesperado: " + e.getMessage());
            cargarDatosFormulario(model);
            return "admin/graduado-form";
        }
    }

    /**
     * Método auxiliar para cargar datos comunes del formulario.
     */
    private void cargarDatosFormulario(Model model) {
        model.addAttribute("provincias", provinciaService.findAll());
        model.addAttribute("facultades", facultadDao.findAll());
        model.addAttribute("colaciones", colacionService.findAllList());
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            EditarGraduadoAdminDTO dto = registroService.obtenerGraduadoParaEdicion(id);
            model.addAttribute("editarGraduadoAdminDTO", dto);
            cargarDatosFormulario(model);
            return "admin/graduado-form-editar"; // nueva vista
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo cargar el graduado: " + e.getMessage());
            return "redirect:/admin/graduados";
        }
    }

    @PostMapping("/{id}/editar")
    public String procesarEdicion(
            @PathVariable Long id,
            @Valid @ModelAttribute("editarGraduadoAdminDTO") EditarGraduadoAdminDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarDatosFormulario(model);
            return "admin/graduado-form-editar";
        }

        try {
            registroService.actualizarGraduado(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Graduado actualizado con éxito.");
            return "redirect:/admin/graduados";

        } catch (RuntimeException e) {
            result.reject("unexpected", "Error inesperado: " + e.getMessage());
            cargarDatosFormulario(model);
            return "admin/graduado-form-editar";
        }
    }

    @GetMapping("/buscar")
    public String buscarGraduados(@RequestParam(required = false) String campo,
                                  @RequestParam(required = false) String valor,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size, Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<IUsuarioInfo> usuariosPage;

        // Si no se pasa campo o valor, mostrar todos
        if (campo == null || valor == null || valor.trim().isEmpty()) {
            usuariosPage = usuarioService.findAllGraduados(pageable);
        } else {
            String valorTrim = valor.trim();
            usuariosPage = switch (campo) {
                case "dni" -> {
                    Optional<Usuario> usuarioOpt = usuarioService.findByDni(valorTrim);
                    if (usuarioOpt.isPresent()) {
                        IUsuarioInfo usuarioInfo = new UsuarioInfoImpl(usuarioOpt.get());
                        yield new PageImpl<>(List.of(usuarioInfo), pageable, 1);
                    } else {
                        yield Page.empty(pageable);
                    }
                }
                case "email" -> usuarioService.findByEmailContainingIgnoreCase(valorTrim, pageable);
                case "nombre" -> usuarioService.findByNombreContainingIgnoreCase(valorTrim, pageable);
                case "apellido" -> usuarioService.findByApellidoContainingIgnoreCase(valorTrim, pageable);
                case "facultad" -> usuarioService.findByFacultadNombreContainingIgnoreCase(valorTrim, pageable);
                case "carrera" -> usuarioService.findByCarreraNombreContainingIgnoreCase(valorTrim, pageable);
                default -> usuarioService.findAllGraduados(pageable);
            };
        }
        // 🔁 Paginación
        int pagesToShow = 5;
        List<Integer> pageNumbers = PaginacionUtil.calcularRangoPaginas(usuariosPage, pagesToShow);
        // 🔁 Mantener selección y valor en el formulario
        model.addAttribute("page", usuariosPage);
        model.addAttribute("usuarios", usuariosPage.getContent());
        model.addAttribute("pageNumbers", pageNumbers);
        model.addAttribute("campo", campo);
        model.addAttribute("valor", valor);
        model.addAttribute("totalRegistros", usuariosPage.getTotalElements());
        return "admin/graduados";
    }

    /**
     * @param id El ID del Usuario a eliminar.
     * @param redirectAttributes Para enviar mensajes de éxito o error.
     * @return Redirecciona al listado de graduados.
     */
    @PostMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ADMINISTRADOR')") // Mantenemos la seguridad
    public String eliminarGraduado(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            // Llama al servicio que ejecuta la eliminación en cascada manual
            usuarioService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "El graduado (ID: " + id + ") fue eliminado correctamente.");
        } catch (RuntimeException e) {
            // Captura errores lanzados desde el servicio (ej. 'Usuario no encontrado')
            String errorMessage = "Error al eliminar el graduado ID " + id + ": " + e.getMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        } catch (Exception e) {
            // Captura cualquier otro error inesperado
            String errorMessage = "Error inesperado al eliminar el graduado: " + e.getMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/admin/graduados";
    }
}
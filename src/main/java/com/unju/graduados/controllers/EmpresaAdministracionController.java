package com.unju.graduados.controllers;

import com.unju.graduados.dto.AltaEmpresaAdminDTO;
import com.unju.graduados.dto.EditarEmpresaAdminDTO;
import com.unju.graduados.exceptions.DuplicatedResourceException;
import com.unju.graduados.repositories.projections.EmpresaInfoProjection;
import com.unju.graduados.services.IEmpresaAdminService;
import com.unju.graduados.services.IEmpresaService;
import com.unju.graduados.services.IProvinciaService;
import com.unju.graduados.util.PaginacionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/empresas") // 🎯 Mapeo actualizado
@PreAuthorize("hasAnyRole('MODERADOR','ADMINISTRADOR')") // Mismos roles
@RequiredArgsConstructor
@Slf4j
public class EmpresaAdministracionController {

    private final IEmpresaAdminService empresaAdminService;
    private final IProvinciaService provinciaService;

    /**
     * Método auxiliar para cargar datos comunes del formulario de Empresa/Anunciante.
     */
    private void cargarDatosFormularioEmpresa(Model model) {
        model.addAttribute("provincias", provinciaService.findAll());
    }

    /**
     * Listado paginado de anunciantes.
     */
    @GetMapping
    public String listarAnunciantes(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size, Model model) {
        Page<EmpresaInfoProjection> anunciantesPage = empresaAdminService.findAllAnunciantes(PageRequest.of(page, size));
        int pagesToShow = 5;
        List<Integer> pageNumbers = PaginacionUtil.calcularRangoPaginas(anunciantesPage, pagesToShow);

        // 2. Agregar los atributos al modelo
        model.addAttribute("page", anunciantesPage);
        model.addAttribute("anunciantes", anunciantesPage.getContent()); // 🎯 Nombre de atributo de lista
        model.addAttribute("pageNumbers", pageNumbers);
        model.addAttribute("totalRegistros", anunciantesPage.getTotalElements());

        // Atributos de búsqueda para persistencia
        model.addAttribute("campo", null);
        model.addAttribute("valor", null);

        return "admin/empresas/list";
    }

    /**
     * Formulario de alta.
     */
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        if (!model.containsAttribute("altaEmpresaAdminDTO")) {
            model.addAttribute("altaEmpresaAdminDTO", new AltaEmpresaAdminDTO());
        }
        cargarDatosFormularioEmpresa(model);
        return "admin/empresas/create";
    }

    /**
     * Procesar alta interna.
     */
    @PostMapping("/guardar")
    public String procesarAlta(@Valid @ModelAttribute("altaEmpresaAdminDTO") AltaEmpresaAdminDTO dto,
                               BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            cargarDatosFormularioEmpresa(model);
            return "admin/empresas/create";
        }
        try {
            empresaAdminService.registrarAltaInternaAnunciante(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Anunciante registrado con éxito.");
            return "redirect:/admin/empresas"; // Mejor 'redirect:/admin/empresas' para ir al @GetMapping
        } catch (DuplicatedResourceException e) {
            result.rejectValue(e.getFieldName(), "duplicated", e.getMessage());
            cargarDatosFormularioEmpresa(model);
            return "admin/empresas/create";
        } catch (RuntimeException e) {
            log.error("Fallo inesperado al registrar alta de anunciante. Causa:", e);
            result.reject("unexpected", "Error inesperado: " + e.getMessage());
            cargarDatosFormularioEmpresa(model);
            return "admin/empresas/create";
        }
    }

    @GetMapping("/{id}") // URL: /admin/empresas/{id}
    public String mostrarDetalles(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Reusamos el mismo DTO y método de servicio que la edición
            EditarEmpresaAdminDTO dto = empresaAdminService.obtenerAnuncianteParaEdicion(id);

            // Usamos un nombre más claro para el modelo de solo lectura
            model.addAttribute("detalleEmpresaDTO", dto);

            // No es necesario cargar listas como provincias/facultades si solo se muestran nombres

            return "admin/empresas/detail"; // <-- Nueva vista
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo cargar el anunciante: " + e.getMessage());
            return "redirect:/admin/empresas";
        }
    }

    /**
     * EDICION - Muestra el formulario con los datos cargados.
     */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            EditarEmpresaAdminDTO dto = empresaAdminService.obtenerAnuncianteParaEdicion(id);
            model.addAttribute("editarEmpresaAdminDTO", dto);
            cargarDatosFormularioEmpresa(model); // Cargar listas/provincias
            return "admin/empresas/edit";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo cargar el anunciante: " + e.getMessage());
            return "redirect:/admin/empresas"; // Ir a la vista de listado
        }
    }

    /**
     * EDICION - Procesa el formulario de edición.
     */
    @PostMapping("/{id}/editar")
    public String procesarEdicion(@PathVariable Long id,
                                  // ✅ CORRECCIÓN: Usar el DTO correcto en el ModelAttribute
                                  @Valid @ModelAttribute("editarEmpresaAdminDTO") EditarEmpresaAdminDTO dto,
                                  BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            // Si hay errores de validación, se recarga la vista de edición,
            // pero necesitamos el ID en la URL para que el formulario funcione.
            cargarDatosFormularioEmpresa(model);
            return "admin/empresas/edit";
        }

        try {
            empresaAdminService.actualizarAnunciante(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Anunciante actualizado con éxito.");

            // ✅ CORRECCIÓN: Redireccionar a la lista principal después de un éxito.
            return "redirect:/admin/empresas";

        } catch (RuntimeException e) {
            // Si hay un error de lógica/servicio (ej. DNI duplicado), lo rechazamos.
            // Si el error es una DuplicatedResourceException, necesitarías mapear el campo específico (similar al método POST de alta).

            if (e instanceof DuplicatedResourceException duplicatedException) {
                result.rejectValue(duplicatedException.getFieldName(), "duplicated", duplicatedException.getMessage());
            } else {
                result.reject("unexpected", "Error inesperado al actualizar: " + e.getMessage());
            }

            // Si falla la edición, volvemos al formulario para mostrar los errores.
            cargarDatosFormularioEmpresa(model);
            return "admin/empresas/edit";
        }
    }

    @GetMapping("/buscar")
    public String buscarAnunciantes(@RequestParam(required = false) String campo,
                                    @RequestParam(required = false) String valor,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size, Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EmpresaInfoProjection> anunciantesPage;
        if (campo == null || valor == null || valor.trim().isEmpty()) {
            anunciantesPage = empresaAdminService.findAllAnunciantes(pageable);
        } else {
            String valorTrim = valor.trim();
            anunciantesPage = switch (campo) {
                case "cuit" -> empresaAdminService.findByCuitContaining(valorTrim, pageable);
                case "email" -> empresaAdminService.findByEmailContainingIgnoreCase(valorTrim, pageable);
                case "nombre_empresa" -> empresaAdminService.findByNombreEmpresaContainingIgnoreCase(valorTrim, pageable);
                default -> empresaAdminService.findAllAnunciantes(pageable);
            };
        }
        int pagesToShow = 5;
        List<Integer> pageNumbers = PaginacionUtil.calcularRangoPaginas(anunciantesPage, pagesToShow);
        model.addAttribute("page", anunciantesPage);
        model.addAttribute("anunciantes", anunciantesPage.getContent()); // 🎯 Nombre de atributo de lista
        model.addAttribute("pageNumbers", pageNumbers);
        model.addAttribute("campo", campo);
        model.addAttribute("valor", valor);
        model.addAttribute("totalRegistros", anunciantesPage.getTotalElements());
        return "admin/empresas/list"; // 🎯 Vista actualizada
    }

    /**
     * Eliminar
     */
    @PostMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ADMINISTRADOR')") // Mantenemos la seguridad
    public String eliminarAnunciante(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            empresaAdminService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "El anunciante (ID: " + id + ") fue eliminado correctamente.");
        } catch (RuntimeException e) {
            String errorMessage = "Error al eliminar el anunciante ID " + id + ": " + e.getMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        } catch (Exception e) {
            String errorMessage = "Error inesperado al eliminar el anunciante: " + e.getMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/admin/empresas"; // Redireccionamos a la lista principal
    }
}

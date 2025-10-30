package com.unju.graduados.controllers;

import com.unju.graduados.dto.AltaEmpresaAdminDTO;
import com.unju.graduados.dto.EditarEmpresaAdminDTO;
import com.unju.graduados.exceptions.DuplicatedResourceException;
import com.unju.graduados.repositories.projections.EmpresaInfoProjection;
import com.unju.graduados.services.IEmpresaAdminService;
import com.unju.graduados.services.IEmpresaService;
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

    private final IEmpresaService anuncianteService;
    private final IEmpresaAdminService anuncianteAdminService;

    /**
     * Listado paginado de anunciantes.
     */
    @GetMapping
    public String listarAnunciantes(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size, Model model) {
        Page<EmpresaInfoProjection> anunciantesPage = anuncianteAdminService.findAllAnunciantes(PageRequest.of(page, size));
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
        // 🎯 DTO actualizado
        if (!model.containsAttribute("altaAnuncianteAdminDTO")) {
            model.addAttribute("altaAnuncianteAdminDTO", new AltaEmpresaAdminDTO());
        }
        // Si el formulario de Anunciante requiere datos externos, crea/llama a cargarDatosFormulario(model);
        return "admin/empresas/create"; // 🎯 Vista actualizada
    }

    /**
     * Procesar alta interna.
     */
    @PostMapping("/guardar")
    public String procesarAlta(@Valid @ModelAttribute("altaAnuncianteAdminDTO") AltaEmpresaAdminDTO dto, // 🎯 DTO actualizado
                               BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Si el formulario de Anunciante requiere datos externos, llama a cargarDatosFormulario(model);
            return "admin/anunciantes/create";
        }
        try {
            // 🎯 Servicio actualizado
            anuncianteAdminService.registrarAltaInternaAnunciante(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Anunciante registrado con éxito.");
            return "redirect:/admin/anunciantes"; // Redireccionamos a la lista principal

        } catch (DuplicatedResourceException e) {
            result.rejectValue(e.getFieldName(), "duplicated", e.getMessage());
            // Si el formulario de Anunciante requiere datos externos, llama a cargarDatosFormulario(model);
            return "admin/anunciantes/create";

        } catch (RuntimeException e) {
            log.error("Fallo inesperado al registrar alta de anunciante. Causa:", e);
            result.reject("unexpected", "Error inesperado: " + e.getMessage());
            // Si el formulario de Anunciante requiere datos externos, llama a cargarDatosFormulario(model);
            return "admin/anunciantes/create";
        }
    }

    /**
     * EDICION
     */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // 🎯 DTO y Servicio actualizados
            EditarEmpresaAdminDTO dto = anuncianteAdminService.obtenerAnuncianteParaEdicion(id);
            model.addAttribute("editarAnuncianteAdminDTO", dto);
            // Si el formulario de Anunciante requiere datos externos, crea/llama a cargarDatosFormulario(model);
            return "admin/anunciantes/edit"; // 🎯 Vista actualizada
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo cargar el anunciante: " + e.getMessage());
            return "redirect:/admin/anunciantes";
        }
    }

    @PostMapping("/{id}/editar")
    public String procesarEdicion(
            @PathVariable Long id,
            @Valid @ModelAttribute("editarAnuncianteAdminDTO") EditarEmpresaAdminDTO dto, // 🎯 DTO actualizado
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Si el formulario de Anunciante requiere datos externos, llama a cargarDatosFormulario(model);
            return "admin/anunciantes/edit";
        }
        try {
            // 🎯 Servicio actualizado
            anuncianteAdminService.actualizarAnunciante(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Anunciante actualizado con éxito.");
            return "redirect:/admin/anunciantes";
        } catch (RuntimeException e) {
            result.reject("unexpected", "Error inesperado: " + e.getMessage());
            // Si el formulario de Anunciante requiere datos externos, llama a cargarDatosFormulario(model);
            return "admin/anunciantes/edit";
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
            anunciantesPage = anuncianteAdminService.findAllAnunciantes(pageable);
        } else {
            String valorTrim = valor.trim();
            anunciantesPage = switch (campo) {
                case "cuit" -> anuncianteAdminService.findByCuitContaining(valorTrim, pageable);
                case "email" -> anuncianteAdminService.findByEmailContainingIgnoreCase(valorTrim, pageable);
                case "nombre_empresa" -> anuncianteAdminService.findByNombreEmpresaContainingIgnoreCase(valorTrim, pageable);
                default -> anuncianteAdminService.findAllAnunciantes(pageable);
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
        return "admin/anunciantes/list"; // 🎯 Vista actualizada
    }

    /**
     * Eliminar
     */
    @PostMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ADMINISTRADOR')") // Mantenemos la seguridad
    public String eliminarAnunciante(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            // 🎯 Servicio actualizado
            anuncianteService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "El anunciante (ID: " + id + ") fue eliminado correctamente.");
        } catch (RuntimeException e) {
            String errorMessage = "Error al eliminar el anunciante ID " + id + ": " + e.getMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        } catch (Exception e) {
            String errorMessage = "Error inesperado al eliminar el anunciante: " + e.getMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/admin/anunciantes"; // Redireccionamos a la lista principal
    }
}

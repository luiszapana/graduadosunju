package com.unju.graduados.controllers;

import com.unju.graduados.dto.UsuarioPerfilDto;
import com.unju.graduados.model.Perfil; // Para la lista de filtros
import com.unju.graduados.repositories.projections.UsuarioInfoProjection;
import com.unju.graduados.services.IPerfilService; // Asumimos un servicio para obtener la lista de perfiles (Admin, Mod, User)
import com.unju.graduados.services.IUsuarioPerfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/admin/perfiles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')") // Solo Administradores pueden acceder a este controlador
public class PerfilAdminController {

    private final IUsuarioPerfilService usuarioPerfilService;

    /**
     * Muestra la tabla de usuarios filtrables por perfil.
     * El perfil GRADUADO (ID 4) se excluye de la lógica de filtrado inicial.
     */
    @GetMapping
    public String listPerfiles(@RequestParam(name = "perfilId", required = false) Long perfilId,
                               @PageableDefault(size = 10, sort = "apellido") Pageable pageable, Model model) {
        Page<UsuarioInfoProjection> usuariosPage = usuarioPerfilService.findUsuariosByPerfilId(perfilId, pageable);
        List<Perfil> perfilesFiltro = List.of();
        model.addAttribute("usuariosPage", usuariosPage);
        model.addAttribute("perfilesFiltro", perfilesFiltro);
        model.addAttribute("perfilIdSeleccionado", perfilId);
        model.addAttribute("totalRegistros", usuariosPage.getTotalElements());
        return "admin/perfiles/list";
    }

    // ----------------------------------------------------------------------------------
    // Métodos para Edición de Perfiles
    // ----------------------------------------------------------------------------------

    /**
     * Muestra el formulario para ver y modificar los perfiles asignados a un usuario.
     */
    @GetMapping("/{id}/editar")
    public String showEditForm(@PathVariable("id") Long usuarioId, Model model) {
        UsuarioPerfilDto usuarioPerfiles = usuarioPerfilService.getUsuarioPerfiles(usuarioId);
        model.addAttribute("usuarioPerfiles", usuarioPerfiles);
        return "admin/perfiles/edit";
    }

    /**
     * Procesa la solicitud POST para actualizar la lista de perfiles del usuario.
     */
    @PostMapping("/{id}/editar")
    public String updatePerfiles(@PathVariable("id") Long usuarioId,
                                 @RequestParam(name = "perfilIds", required = false) List<Long> perfilIds, RedirectAttributes ra) {
        if (perfilIds == null) {// Si perfilIds es nulo (no se marcó ningún checkbox), se envía una lista vacía para quitar todos los roles
            perfilIds = List.of();
        }
        UsuarioPerfilDto dto = usuarioPerfilService.getUsuarioPerfiles(usuarioId);
        String infoUsuario = dto.getDni() + " (" + dto.getNombre() + " " + dto.getApellido() + ")";
        usuarioPerfilService.updateUsuarioPerfiles(usuarioId, perfilIds);
        ra.addFlashAttribute("successMessage", "Perfiles del usuario " + infoUsuario + " actualizados correctamente.");
        return "redirect:/admin/perfiles";
    }
}
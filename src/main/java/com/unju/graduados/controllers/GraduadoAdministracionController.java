package com.unju.graduados.controllers;

import com.unju.graduados.dto.AltaGraduadoAdminDTO;
import com.unju.graduados.model.Usuario;
import com.unju.graduados.model.repositories.IFacultadRepository;
import com.unju.graduados.services.IColacionService;
import com.unju.graduados.services.IProvinciaService;
import com.unju.graduados.services.IRegistroService;
import com.unju.graduados.services.IUsuarioService;
import com.unju.graduados.util.PaginacionUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasAnyRole('MODERADOR','ADMINISTRADOR')")
@RequiredArgsConstructor
public class GraduadoAdministracionController {
    private final IUsuarioService usuarioService;
    // Necesitamos el servicio de registro para la lógica de guardado
    private final IRegistroService registroService;
    private final IProvinciaService provinciaService;
    private final IFacultadRepository facultadDao;
    private final IColacionService colacionService;

    @GetMapping
    public String listarUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<Usuario> usuariosPage = usuarioService.findAll(PageRequest.of(page, size));
        int pagesToShow = 5;
        List<Integer> pageNumbers = PaginacionUtil.calcularRangoPaginas(usuariosPage, pagesToShow);

        // 2. Agrega los atributos al modelo
        model.addAttribute("page", usuariosPage);
        model.addAttribute("usuarios", usuariosPage.getContent());
        model.addAttribute("pageNumbers", pageNumbers);
        return "admin/usuarios";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("altaGraduadoAdminDTO", new AltaGraduadoAdminDTO());
        cargarDatosFormulario(model); // Método auxiliar para cargar listas
        return "admin/graduado-form"; // Creamos esta nueva plantilla
    }

    @PostMapping("/nuevo")
    public String procesarAlta(@Valid @ModelAttribute("dto") AltaGraduadoAdminDTO dto,
                               BindingResult result,
                               Model model) {

        if (result.hasErrors()) {
            cargarDatosFormulario(model);
            return "admin/graduado-form";
        }

        try {
            // Llama al nuevo método de servicio para el alta interna
            registroService.registrarAltaInternaGraduado(dto);

        } catch (RuntimeException e) {
            // Manejar errores de negocio (ej. DNI duplicado, email ya registrado)
            result.reject(null, "Error al registrar el graduado: " + e.getMessage());
            cargarDatosFormulario(model);
            return "admin/graduado-form";
        }

        // Redireccionar al listado de usuarios o a la página de éxito
        return "redirect:/admin/usuarios";
    }

    // Método auxiliar para cargar listas (Provincias, Facultades, Colaciones)
    private void cargarDatosFormulario(Model model) {
        model.addAttribute("provincias", provinciaService.findAll());
        model.addAttribute("facultades", facultadDao.findAll());

        // Cargar colaciones. Si IColacionService.findAll() devuelve Page<Colacion>,
        // usamos .getContent() para obtener la List<Colacion> que el select necesita.
        // Asumo que el registro interno necesita todas las colaciones disponibles
        model.addAttribute("colaciones", colacionService.findAllList());

        // Nota: Asegúrate que IColacionService.findAllList() esté implementado.
        // Si no tienes findAllList(), usa:
        // model.addAttribute("colaciones", colacionService.findAll(PageRequest.of(0, 100)).getContent());
    }

    @PostMapping("/graduados/guardar") // Ruta para procesar el formulario
    public String registrarGraduado(@ModelAttribute("altaGraduadoAdminDTO") AltaGraduadoAdminDTO dto,
                                    // @RequestParam("avatar") MultipartFile avatar, // Si quieres manejar la imagen separada
                                    RedirectAttributes redirectAttributes) {

        // Aquí iría la llamada al servicio
        registroService.registrarAltaInternaGraduado(dto);

        // Manejo de redirección y éxito
        redirectAttributes.addFlashAttribute("mensaje", "Graduado registrado con éxito.");
        return "redirect:/admin/graduados";
    }

    /**
     * Procesa la solicitud para eliminar un Usuario por su ID (que representa al graduado).
     * @param id El ID del Usuario a eliminar.
     * @param redirectAttributes Para enviar mensajes de éxito o error.
     * @return Redirecciona al listado de usuarios.
     */
    @PostMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ADMINISTRADOR')") // Mantenemos la seguridad
    public String eliminarGraduado(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            // Llama al servicio que ejecuta la eliminación en cascada manual
            usuarioService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "El usuario (ID: " + id + ") fue eliminado correctamente.");

        } catch (RuntimeException e) {
            // Captura errores lanzados desde el servicio (ej. 'Usuario no encontrado')
            String errorMessage = "Error al eliminar el usuario ID " + id + ": " + e.getMessage();
            System.err.println(errorMessage);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        } catch (Exception e) {
            // Captura cualquier otro error inesperado
            String errorMessage = "Error inesperado al eliminar el usuario: " + e.getMessage();
            System.err.println(errorMessage);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }

        // Redirige al listado de usuarios
        return "redirect:/admin/usuarios";
    }
}
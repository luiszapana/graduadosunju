package com.unju.graduados.controllers;

import com.unju.graduados.dto.AnuncioDTO;
import com.unju.graduados.model.AnuncioTipo;
import com.unju.graduados.model.Usuario;
import com.unju.graduados.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AnuncioController {

    private static final Logger logger = LoggerFactory.getLogger(AnuncioController.class);
    private final IAnuncioService anuncioService;
    private final ITipoAnuncioService tipoService;
    private final ICarreraService carreraService;
    private final IFacultadService facultadService;
    private final IUsuarioBaseService usuarioBaseService; // <- Bean que no puede ser inyectado por lombok

    // CONSTRUCTOR MANUAL CON @Autowired y @Qualifier por usuarioBaseService
    @Autowired
    public AnuncioController(IAnuncioService anuncioService, ITipoAnuncioService tipoService,
                             ICarreraService carreraService, IFacultadService facultadService,
                             @Qualifier("usuarioBaseServiceImpl")
                             IUsuarioBaseService usuarioBaseService) {
            this.anuncioService = anuncioService;
            this.tipoService = tipoService;
            this.carreraService = carreraService;
            this.facultadService = facultadService;
            this.usuarioBaseService = usuarioBaseService;
    }

    @GetMapping({"/"})
    public String homeRedirect() {
        return "redirect:/login";
    }

    @GetMapping("/anuncios")
    public String listar(@RequestParam(required = false) Long tipoId,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime desde,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime hasta,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model) {
        Page<AnuncioDTO> anunciosPage = anuncioService.listar(tipoId, desde, hasta, PageRequest.of(page, size));
        List<AnuncioTipo> tipos = tipoService.listar();
        logger.info("Anuncios encontrados: {}", anunciosPage.getContent());

        model.addAttribute("anuncios", anunciosPage.getContent());
        model.addAttribute("page", anunciosPage);
        model.addAttribute("tipos", tipos);
        model.addAttribute("filtroTipoId", tipoId);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        int totalPages = anunciosPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = java.util.stream.IntStream.range(0, totalPages)
                    .boxed()
                    .toList();
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "anuncios/list";
    }

    @GetMapping("/anuncios/{id}")
    public String verDetalleAnuncio(@PathVariable Long id, Model model) {
        AnuncioDTO anuncio = anuncioService.obtener(id);
        model.addAttribute("anuncio", anuncio);
        return "anuncios/detail";
    }

    /*
     *   *** Alta de Anuncios ***
     */
    @GetMapping("/anuncios/nuevo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR', 'EMPRESA')")
    public String mostrarFormulario(Model model) {
        // Muestra el formulario anuncios/form
        model.addAttribute("anuncio", new AnuncioDTO());
        model.addAttribute("tipos", tipoService.listar());
        model.addAttribute("carreras", carreraService.findAll());   // Cargar todas las carreras
        model.addAttribute("facultades", facultadService.findAll()); // Cargar todas las facultades
        return "anuncios/create";
    }

    @PostMapping("/anuncios")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR', 'EMPRESA')") // Se recomienda agregar PreAuthorize aquí
    public String crear(@Valid @ModelAttribute("anuncio") AnuncioDTO dto,
                        BindingResult result, Model model, Principal principal) {
        if (result.hasErrors()) {
            model.addAttribute("tipos", tipoService.listar());
            model.addAttribute("carreras", carreraService.findAll());
            model.addAttribute("facultades", facultadService.findAll());
            return "anuncios/create";
        }

        // 2. Lógica para obtener el ID del usuario creador
        // Obtiene el nombre de usuario (típicamente el email) del usuario logueado
        String nombreUsuario = principal.getName();
        // Busca la entidad Usuario en la base de datos
        Optional<Usuario> usuarioOpt = usuarioBaseService.findByNombreLogin(nombreUsuario);
        if (usuarioOpt.isEmpty()) {  // Manejo de error si el usuario autenticado no se encuentra en la DB
            logger.error("Usuario autenticado no encontrado para el login: {}", nombreUsuario);
            // Redirige al login o a una página de error con un mensaje
            return "redirect:/login?error=user_not_found";
        }
        // Extrae el ID del usuario
        Long idUsuarioCreador = usuarioOpt.get().getId();

        // 3. Delegar al Servicio: El servicio maneja la persistencia del anuncio, el targeting, y el envío de emails.
        anuncioService.crear(dto, idUsuarioCreador);
        return "redirect:/anuncios";
    }

    /*
     *   *** Update ***
     */

    @GetMapping("/anuncios/{id}/editar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR', 'EMPRESA')") // Se recomienda agregar PreAuthorize aquí
    public String editarForm(@PathVariable Long id, Model model) {

        model.addAttribute("anuncio", anuncioService.obtener(id));
        model.addAttribute("tipos", tipoService.listar());

        // Cargar las listas de carreras y facultades para la edición
        model.addAttribute("carreras", carreraService.findAll());
        model.addAttribute("facultades", facultadService.findAll());

        return "anuncios/create";
    }

    @PostMapping("/anuncios/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR', 'EMPRESA')") // Se recomienda agregar PreAuthorize aquí
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("anuncio") AnuncioDTO dto, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("tipos", tipoService.listar());

            // Cargar las listas en caso de error de validación durante la actualización
            model.addAttribute("carreras", carreraService.findAll());
            model.addAttribute("facultades", facultadService.findAll());

            return "anuncios/create";
        }

        anuncioService.actualizar(id, dto);
        return "redirect:/anuncios";
    }

    @PostMapping("/anuncios/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        anuncioService.eliminar(id);
        return "redirect:/anuncios";
    }
}

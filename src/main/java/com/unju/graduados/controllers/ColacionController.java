package com.unju.graduados.controllers;

import com.unju.graduados.model.Colacion;
import com.unju.graduados.model.dao.interfaces.IColacionOrdenDao;
import com.unju.graduados.model.dao.interfaces.IFacultadDao;
import com.unju.graduados.model.dao.interfaces.IUniversidadDao;
import com.unju.graduados.service.IColacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;


@Controller
@RequestMapping("/admin/colaciones")
@PreAuthorize("hasAnyRole('MODERADOR','ADMINISTRADOR')")
@RequiredArgsConstructor
public class ColacionController {

    private final IColacionService colacionService;
    private final IColacionOrdenDao ordenRepository;
    private final IUniversidadDao universidadRepository;
    private final IFacultadDao facultadRepository;
    private static final Logger log = LoggerFactory.getLogger(ColacionController.class);


    @GetMapping
    public String listar(Model model) {
        model.addAttribute("colaciones", colacionService.findAll());
        return "colaciones/list";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("colacion", new Colacion());
        cargarDatosComunes(model);
        model.addAttribute("titulo", "Nueva Colación");
        model.addAttribute("navTitulo", "Nueva Colación");
        model.addAttribute("labelAceptar", "Guardar");
        model.addAttribute("fromEdit", false);
        return "colaciones/form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute Colacion colacion, BindingResult result, Model model) {
        log.info(">>> Entrando a guardar con Colacion: {}", colacion);
        if (result.hasErrors()) {
            log.warn(">>> Errores de validación: {}", result.getAllErrors());
            cargarDatosComunes(model);
            model.addAttribute("titulo", "Nueva Colación");
            model.addAttribute("navTitulo", "Nueva Colación");
            model.addAttribute("labelAceptar", "Guardar");
            model.addAttribute("fromEdit", false);
            return "colaciones/form";
        }
        Colacion saved = colacionService.save(colacion);
        log.info(">>> Colacion guardada con ID: {}", saved.getId());
        return "redirect:/admin/colaciones";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("colacion", colacionService.findById(id));
        cargarDatosComunes(model);
        model.addAttribute("titulo", "Editar Colación");
        model.addAttribute("navTitulo", "Editar Colación");
        model.addAttribute("labelAceptar", "Actualizar");
        model.addAttribute("fromEdit", true);
        return "colaciones/form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute Colacion colacion, BindingResult result, Model model) {
        if (result.hasErrors()) {
            cargarDatosComunes(model);
            model.addAttribute("titulo", "Editar Colación");
            model.addAttribute("navTitulo", "Editar Colación");
            model.addAttribute("labelAceptar", "Actualizar");
            model.addAttribute("fromEdit", true);
            return "colaciones/form";
        }
        colacion.setId(id); // aseguramos que el ID sea el de la URL
        colacionService.update(id, colacion);
        return "redirect:/admin/colaciones";
    }

    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        colacionService.delete(id);
        return "redirect:/admin/colaciones";
    }

    /**
     * Método auxiliar para evitar repetir código.
     */
    private void cargarDatosComunes(Model model) {
        model.addAttribute("ordenes", ordenRepository.findAll());
        model.addAttribute("universidades", universidadRepository.findAll());
        model.addAttribute("facultades", facultadRepository.findAll());
    }
}

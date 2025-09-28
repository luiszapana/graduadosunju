package com.unju.graduados.controllers;

import com.unju.graduados.dto.AnuncioDTO;
import com.unju.graduados.model.AnuncioTipo;
import com.unju.graduados.services.IAnuncioService;
import com.unju.graduados.services.ITipoAnuncioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequiredArgsConstructor
public class AnuncioController {

    private static final Logger logger = LoggerFactory.getLogger(AnuncioController.class);

    private final IAnuncioService anuncioService;
    private final ITipoAnuncioService tipoService;

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
        //model.addAttribute("anuncios", anuncios);

        // 🎯 Línea de código para imprimir en la consola
        logger.info("Anuncios encontrados: {}", anunciosPage.getContent());

        model.addAttribute("anuncios", anunciosPage.getContent()); // 👈 solo la lista
        model.addAttribute("page", anunciosPage);                  // 👈 además guardás la página para paginación
        model.addAttribute("tipos", tipos);
        model.addAttribute("filtroTipoId", tipoId);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        return "anuncios/list";
    }

    @GetMapping("/anuncios/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("anuncio", new AnuncioDTO());
        model.addAttribute("tipos", tipoService.listar());
        return "anuncios/form";
    }

    @PostMapping("/anuncios")
    public String crear(@Valid @ModelAttribute("anuncio") AnuncioDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tipos", tipoService.listar());
            return "anuncios/form";
        }
        anuncioService.crear(dto);
        return "redirect:/anuncios";
    }

    @GetMapping("/anuncios/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        model.addAttribute("anuncio", anuncioService.obtener(id));
        model.addAttribute("tipos", tipoService.listar());
        return "anuncios/form";
    }

    @PostMapping("/anuncios/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("anuncio") AnuncioDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tipos", tipoService.listar());
            return "anuncios/form";
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

package com.unju.graduados.rest;

import com.unju.graduados.dto.AnuncioDTO;
import com.unju.graduados.model.AnuncioTipo;
import com.unju.graduados.service.IAnuncioService;
import com.unju.graduados.service.ITipoAnuncioService;
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

@Controller
@RequiredArgsConstructor
public class AnuncioMvcController {

    private final IAnuncioService anuncioService;
    private final ITipoAnuncioService tipoService;

    @GetMapping({"/", "/login"})
    public String loginPage() {
        return "login";
    }

    @GetMapping("/anuncios")
    public String listar(@RequestParam(required = false) Long tipoId,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime desde,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime hasta,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model) {
        Page<AnuncioDTO> anuncios = anuncioService.listar(tipoId, desde, hasta, PageRequest.of(page, size));
        List<AnuncioTipo> tipos = tipoService.listar();
        model.addAttribute("anuncios", anuncios);
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

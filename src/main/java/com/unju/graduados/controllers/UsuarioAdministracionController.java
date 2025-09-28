package com.unju.graduados.controllers;

import com.unju.graduados.model.Usuario;
import com.unju.graduados.services.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
@RequiredArgsConstructor
public class UsuarioAdministracionController {
    private final IUsuarioService usuarioService;

    @GetMapping
    public String listarUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<Usuario> usuariosPage = usuarioService.findAll(PageRequest.of(page, size));

        // Lógica para limitar el rango de números de página mostrados
        int totalPages = usuariosPage.getTotalPages();
        int currentPage = usuariosPage.getNumber();

        // Define cuántos botones de página quieres mostrar (ej. 5)
        int pagesToShow = 5;

        // Calcula el inicio del rango (asegurando que no sea negativo)
        int startPage = Math.max(0, currentPage - (pagesToShow / 2));

        // Calcula el fin del rango (asegurando que no exceda el total de páginas - 1)
        int endPage = Math.min(totalPages - 1, startPage + pagesToShow - 1);

        // Si el rango final se acortó (porque estamos al final),
        // ajustamos el inicio para mantener el número de botones fijo (pagesToShow)
        if (endPage - startPage < pagesToShow - 1) {
            startPage = Math.max(0, endPage - pagesToShow + 1);
        }

        // 1. Crea la lista de números de página del rango calculado
        List<Integer> pageNumbers = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            pageNumbers.add(i);
        }

        // 2. Agrega la lista de números al modelo
        model.addAttribute("page", usuariosPage);
        model.addAttribute("usuarios", usuariosPage.getContent());
        model.addAttribute("pageNumbers", pageNumbers); // <-- ¡NUEVO ATRIBUTO CLAVE!

        return "admin/usuarios";
    }
}
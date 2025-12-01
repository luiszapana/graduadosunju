package com.unju.graduados.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    /**
     * Maneja la solicitud GET /login.
     * Recibe el parámetro 'error' de la URL para gestionar mensajes específicos.
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {

        // El parámetro 'required = false' permite que la URL /login funcione sin el parámetro.
        // 💡 Lógica para detectar el error personalizado enviado desde otros controladores
        if ("user_not_found".equals(error)) {
            // Añadimos un atributo al modelo para que la plantilla login.html pueda usarlo.
            model.addAttribute("customError", "Error de Sesión: El usuario autenticado no fue encontrado en la base de datos.");
        }

        // Nota: Si el parámetro 'error' contiene algo más (ej. error estándar de Spring Security),
        // Thymeleaf lo maneja automáticamente con 'th:if="${param.error}"'.

        return "login"; // Devuelve la plantilla /templates/login.html
    }
}

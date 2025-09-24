package com.unju.graduados.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping
    public String showDashboard() {
        // Renderiza solo la vista en blanco con sidebar dinámico
        return "dashboard";
    }
}


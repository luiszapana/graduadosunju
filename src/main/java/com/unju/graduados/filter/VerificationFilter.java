package com.unju.graduados.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class VerificationFilter extends OncePerRequestFilter {

    private final com.unju.graduados.service.IUsuarioLoginService usuarioLoginService;

    public VerificationFilter(com.unju.graduados.service.IUsuarioLoginService usuarioLoginService) {
        this.usuarioLoginService = usuarioLoginService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Aquí podría validarse que el usuario tenga email verificado/habilitado
        // y si no completó registro, redirigir a completar datos.
        filterChain.doFilter(request, response);
    }
}

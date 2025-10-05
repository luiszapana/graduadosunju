package com.unju.graduados.exceptions;

import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationErrors(MethodArgumentNotValidException ex, Model model) {
        model.addAttribute("message", "Error de validación: " + ex.getMessage());
        return "registro-error";
    }

    @ExceptionHandler(BindException.class)
    public String handleBindErrors(BindException ex, Model model) {
        model.addAttribute("message", "Datos inválidos: " + ex.getMessage());
        return "registro-error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArg(IllegalArgumentException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "registro-error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, Model model) {
        model.addAttribute("message", "Ocurrió un error inesperado");
        return "registro-error";
    }
}

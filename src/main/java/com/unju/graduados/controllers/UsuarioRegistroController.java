package com.unju.graduados.controllers;

import com.unju.graduados.dto.RegistroCredencialesDTO;
import com.unju.graduados.dto.RegistroDTO;
import com.unju.graduados.dto.UsuarioDatosAcademicosDTO;
import com.unju.graduados.model.Usuario;
import com.unju.graduados.model.UsuarioDatosEmpresa;
import com.unju.graduados.model.UsuarioLogin;
import com.unju.graduados.model.repositories.IFacultadRepository;
import com.unju.graduados.services.IColacionService;
import com.unju.graduados.services.IProvinciaService;
import com.unju.graduados.services.impl.RegistroServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/registro")
@RequiredArgsConstructor
public class UsuarioRegistroController {

    private final RegistroServiceImpl registroService;
    private final IFacultadRepository facultadDao;
    private final IProvinciaService provinciaService;
    private final IColacionService colacionService;

    // Paso 1: Registro inicial (credenciales)
    @GetMapping
    public String credencialesForm(Model model) {
        model.addAttribute("dto", new RegistroCredencialesDTO());
        return "registro-credenciales";
    }

    @PostMapping
    public String credencialesSubmit(@Valid @ModelAttribute("dto") RegistroCredencialesDTO dto,
                                     BindingResult result, Model model) {
        if (!result.hasErrors() && !dto.passwordsMatch()) {
            result.rejectValue("confirmPassword", "mismatch", "Las contraseñas no coinciden");
        }
        if (result.hasErrors()) return "registro-credenciales";

        registroService.registrarCredenciales(dto);
        model.addAttribute("email", dto.getEmail());
        return "registro-check-email";
    }

    // Paso 2: Verificación de correo
    @GetMapping("/verificar")
    public String verificar(@RequestParam("token") String token, Model model) {
        Optional<UsuarioLogin> login = registroService.verificarToken(token);
        if (login.isEmpty()) {
            model.addAttribute("message", "Token inválido o expirado");
            return "registro-error";
        }
        RegistroDTO dto = new RegistroDTO();
        dto.setEmail(login.get().getUsuario());

        model.addAttribute("loginId", login.get().getId());
        model.addAttribute("registroDTO", dto);
        model.addAttribute("email", login.get().getUsuario());
        model.addAttribute("provincias", provinciaService.findAll());

        return "registro-datos-personales";
    }

    // Paso 3: Completar datos personales
    @PostMapping("/datos-personales")
    public String guardarDatosPersonales(@RequestParam Long loginId,
                                         @Valid @ModelAttribute("registroDTO") RegistroDTO dto,
                                         @RequestParam("tipo") String tipo,
                                         Model model) {
        try {
            boolean esEgresado = "Egresado".equalsIgnoreCase(tipo);
            Usuario usuario = registroService.completarDatosPersonales(loginId, dto, esEgresado);
            registroService.asignarPerfilPorTipo(loginId, esEgresado);

            if (esEgresado) {
                return "redirect:/registro/datos-academicos?loginId=" + loginId + "&usuarioId=" + usuario.getId();
            } else {
                model.addAttribute("loginId", loginId);
                model.addAttribute("usuarioId", usuario.getId());
                model.addAttribute("empresa", new UsuarioDatosEmpresa());
                return "registro-datos-empresa";
            }
        } catch (RuntimeException e) {
            model.addAttribute("error", "Error al procesar los datos: " + e.getMessage());
            model.addAttribute("provincias", provinciaService.findAll());
            return "registro-datos-personales";
        }
    }

    // Paso 4 (GET): Mostrar formulario de datos académicos
    @GetMapping("/datos-academicos")
    public String mostrarFormularioAcademicos(@RequestParam Long loginId,
                                              @RequestParam Long usuarioId,
                                              Model model) {
        UsuarioDatosAcademicosDTO dto = new UsuarioDatosAcademicosDTO();
        dto.setUsuarioId(usuarioId);
        dto.setIdUniversidad(1L); // preseleccionar UNJu

        model.addAttribute("academicos", dto);
        model.addAttribute("facultades", facultadDao.findAll());
        model.addAttribute("colaciones", colacionService.findAll()); // 🔥 AQUI

        model.addAttribute("loginId", loginId);
        model.addAttribute("usuarioId", usuarioId);
        return "registro-datos-academicos";
    }

    // Paso 4 (POST): Guardar datos académicos
    @PostMapping("/datos-academicos")
    public String guardarAcademicos(@RequestParam Long loginId,
                                    @ModelAttribute("academicos") UsuarioDatosAcademicosDTO dto,
                                    @RequestParam(name = "tambienEmpresa", defaultValue = "false") boolean tambienEmpresa,
                                    Model model) {
        Long usuarioId = dto.getUsuarioId();

        registroService.validarLoginUsuario(loginId, usuarioId);
        registroService.guardarDatosAcademicos(usuarioId, dto);

        if (tambienEmpresa) {
            model.addAttribute("loginId", loginId);
            model.addAttribute("usuarioId", usuarioId);
            model.addAttribute("empresa", new UsuarioDatosEmpresa());
            registroService.asignarPerfilesGraduadoYUsuario(loginId);
            return "registro-datos-empresa";
        }

        return "redirect:/registro/bienvenida";
    }

    @PostMapping("/datos-empresa")
    public String guardarEmpresa(@RequestParam Long loginId,
                                 @RequestParam Long usuarioId,
                                 @Valid @ModelAttribute("empresa") UsuarioDatosEmpresa empresa,
                                 BindingResult result,
                                 Model model) {
        registroService.validarLoginUsuario(loginId, usuarioId);
        if (result.hasErrors()) {
            model.addAttribute("loginId", loginId);
            model.addAttribute("usuarioId", usuarioId);
            return "registro-datos-empresa";
        }
        registroService.guardarDatosEmpresa(usuarioId, empresa);
        return "redirect:/registro/bienvenida";
    }

    @GetMapping("/bienvenida")
    public String bienvenida() {
        return "registro-bienvenida";
    }
}

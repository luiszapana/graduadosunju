package com.unju.graduados.rest;

import com.unju.graduados.dto.RegistroCredencialesDTO;
import com.unju.graduados.model.Usuario;
import com.unju.graduados.model.UsuarioDatosAcademicos;
import com.unju.graduados.model.UsuarioDatosEmpresa;
import com.unju.graduados.model.UsuarioLogin;
import com.unju.graduados.service.impl.RegistroServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/registro")
public class RegistroMvcController {

    private final RegistroServiceImpl registroService;

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
        // guardar loginId en modelo para siguiente paso
        model.addAttribute("loginId", login.get().getId());
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("email", login.get().getUsuario());
        return "registro-datos-personales";
    }

    // Paso 3: Completar datos personales
    @PostMapping("/datos-personales")
    public String guardarDatosPersonales(@RequestParam Long loginId,
                                         @ModelAttribute("usuario") Usuario usuario,
                                         @RequestParam("tipo") String tipo,
                                         Model model) {
        boolean esEgresado = "Egresado".equalsIgnoreCase(tipo);
        Usuario u = registroService.completarDatosPersonales(loginId, usuario, esEgresado);
        registroService.asignarPerfilPorTipo(loginId, esEgresado);
        model.addAttribute("loginId", loginId);
        model.addAttribute("usuarioId", u.getId());
        if (esEgresado) {
            model.addAttribute("academicos", new UsuarioDatosAcademicos());
            return "registro-datos-academicos";
        } else {
            model.addAttribute("empresa", new UsuarioDatosEmpresa());
            return "registro-datos-empresa";
        }
    }

    // Paso 4: Formularios condicionales
    @PostMapping("/datos-academicos")
    public String guardarAcademicos(@RequestParam Long loginId,
                                    @RequestParam Long usuarioId,
                                    @ModelAttribute("academicos") UsuarioDatosAcademicos academicos,
                                    @RequestParam(name = "tambienEmpresa", defaultValue = "false") boolean tambienEmpresa,
                                    Model model) {
        registroService.validarLoginUsuario(loginId, usuarioId);
        registroService.guardarDatosAcademicos(usuarioId, academicos);
        if (tambienEmpresa) {
            // paso 5: opcional empresa
            model.addAttribute("loginId", loginId);
            model.addAttribute("usuarioId", usuarioId);
            model.addAttribute("empresa", new UsuarioDatosEmpresa());
            // Asignar ambos perfiles al final del flujo
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

    // Pantalla de bienvenida final
    @GetMapping("/bienvenida")
    public String bienvenida() {
        return "registro-bienvenida";
    }
}

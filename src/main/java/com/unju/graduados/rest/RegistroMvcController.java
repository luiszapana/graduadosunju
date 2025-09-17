package com.unju.graduados.rest;

import com.unju.graduados.dto.RegistroCredencialesDTO;
import com.unju.graduados.dto.UsuarioDatosAcademicosDTO;
import com.unju.graduados.model.*;
import com.unju.graduados.model.dao.interfaces.IFacultadDao;
import com.unju.graduados.service.impl.RegistroServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/registro")
@RequiredArgsConstructor
public class RegistroMvcController {

    private final RegistroServiceImpl registroService;
    private final IFacultadDao facultadDao; // ⚠️ Necesario para cargar la lista de facultades

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
        usuario.setImagen(null); // No viene del form

        boolean esEgresado = "Egresado".equalsIgnoreCase(tipo);
        Usuario u = registroService.completarDatosPersonales(loginId, usuario, esEgresado);
        registroService.asignarPerfilPorTipo(loginId, esEgresado);

        if (esEgresado) {
            // 🔀 Redirigir al GET para que cargue correctamente el combo de facultades
            return "redirect:/registro/datos-academicos?loginId=" + loginId + "&usuarioId=" + u.getId();
        } else {
            model.addAttribute("loginId", loginId);
            model.addAttribute("usuarioId", u.getId());
            model.addAttribute("empresa", new UsuarioDatosEmpresa());
            return "registro-datos-empresa";
        }
    }

    // Paso 4 (GET): Mostrar formulario de datos académicos
    @GetMapping("/datos-academicos")
    public String mostrarFormularioAcademicos(@RequestParam Long loginId,
                                              @RequestParam Long usuarioId,
                                              Model model) {
        UsuarioDatosAcademicos acad = new UsuarioDatosAcademicos();
        acad.setUsuario(null); // Se setea en el servicio
        model.addAttribute("academicos", acad);

        // 📌 Cargar lista de facultades para el combo
        List<Facultad> facultades = facultadDao.findAll();
        model.addAttribute("facultades", facultades);

        model.addAttribute("loginId", loginId);
        model.addAttribute("usuarioId", usuarioId);
        return "registro-datos-academicos";
    }

    // Paso 4 (POST): Guardar datos académicos
    @PostMapping("/datos-academicos")
    public String guardarAcademicos(@RequestParam Long loginId,
                                    @RequestParam Long usuarioId,
                                    @ModelAttribute("academicos") UsuarioDatosAcademicosDTO dto,
                                    @RequestParam(name = "tambienEmpresa", defaultValue = "false") boolean tambienEmpresa,
                                    Model model) {
        registroService.validarLoginUsuario(loginId, usuarioId);
        registroService.guardarDatosAcademicos(usuarioId, dto); // <-- cambia a DTO

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

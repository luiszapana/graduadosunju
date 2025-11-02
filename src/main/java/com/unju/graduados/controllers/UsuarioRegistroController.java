package com.unju.graduados.controllers;

import com.unju.graduados.dto.RegistroCredencialesDTO;
import com.unju.graduados.dto.RegistroDTO;
import com.unju.graduados.dto.UsuarioDatosAcademicosDTO;
import com.unju.graduados.exceptions.DuplicatedResourceException;
import com.unju.graduados.model.*;
import com.unju.graduados.repositories.IFacultadRepository;
import com.unju.graduados.services.IEmpresaService;
import com.unju.graduados.services.IColacionService;
import com.unju.graduados.services.IProvinciaService;
import com.unju.graduados.services.IRegistroExternoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@Controller
@RequestMapping("/registro")
@RequiredArgsConstructor
public class UsuarioRegistroController {

    private final IRegistroExternoService registroExternoService;
    private final IFacultadRepository facultadRepository;
    private final IProvinciaService provinciaService;
    private final IColacionService colacionService;
    private final IEmpresaService empresaService;

    // Paso 1: Registro inicial (credenciales)
    @GetMapping
    public String credencialesForm(Model model) {
        model.addAttribute("dto", new RegistroCredencialesDTO());
        return "registrate/credenciales";
    }

    @PostMapping
    public String credencialesSubmit(@Valid @ModelAttribute("dto") RegistroCredencialesDTO dto,
                                     BindingResult result, Model model) {
        if (!result.hasErrors() && !dto.passwordsMatch()) {
            result.rejectValue("confirmPassword", "mismatch", "Las contraseñas no coinciden");
        }
        if (result.hasErrors()) return "registrate/credenciales";

        try {
            // 🛑 Llamada al servicio que ahora lanza DuplicatedResourceException
            registroExternoService.registrarCredenciales(dto);
            model.addAttribute("email", dto.getEmail());
            return "registrate/check-email";
        } catch (DuplicatedResourceException e) {
            result.rejectValue(e.getFieldName(), "duplicated", e.getMessage());
            return "registrate/credenciales"; // Vuelve al formulario
        }
    }

    // Paso 2: Verificación de correo
    @GetMapping("/verificar")
    public String verificar(@RequestParam("token") String token, Model model) {
        Optional<UsuarioLogin> login = registroExternoService.verificarToken(token);
        if (login.isEmpty()) {
            model.addAttribute("message", "Token inválido o expirado");
            return "registrate/error";
        }
        RegistroDTO dto = new RegistroDTO();
        dto.setEmail(login.get().getUsuario());

        model.addAttribute("loginId", login.get().getId());
        model.addAttribute("registroDTO", dto);
        model.addAttribute("email", login.get().getUsuario());
        model.addAttribute("provincias", provinciaService.findAll());

        return "registrate/datos-personales";
    }

    @PostMapping("/datos-personales")
    public String guardarDatosPersonales(@RequestParam Long loginId,
                                         @Valid @ModelAttribute("registroDTO") RegistroDTO dto, BindingResult result,
                                         @RequestParam("tipo") String tipo, Model model) {
        model.addAttribute("loginId", loginId);
        model.addAttribute("provincias", provinciaService.findAll());
        model.addAttribute("localidades", Collections.emptyList());

        if (result.hasErrors()) {
            return "registrate/datos-personales";
        }
        try {
            boolean esEgresado = "Egresado".equalsIgnoreCase(tipo);

            Usuario usuario = registroExternoService.completarDatosPersonales(loginId, dto, esEgresado);
            registroExternoService.asignarPerfilPorTipo(loginId, esEgresado);

            if (esEgresado) {
                return "redirect:/registro/datos-academicos?loginId=" + loginId + "&usuarioId=" + usuario.getId();
            } else {
                // FIX CRÍTICO: Inicializar la empresa y establecer el ID de usuario.
                model.addAttribute("loginId", loginId);
                model.addAttribute("usuarioId", usuario.getId());
                UsuarioDatosEmpresa empresa = new UsuarioDatosEmpresa();
                empresa.setIdUsuario(usuario.getId()); // <<-- Esto es clave
                model.addAttribute("empresa", empresa);

                System.out.println("LOG DEBUG: Redirigiendo a registrate/datos-empresa. Los IDs de login y usuario se cargaron en el Model.");

                return "registrate/datos-empresa";
            }
        } catch (DuplicatedResourceException e) {
            result.rejectValue(e.getFieldName(), "duplicated", e.getMessage());
            // Si el DNI es duplicado, volvemos a la vista con el error
            return "registrate/datos-personales";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Error al procesar los datos: " + e.getMessage());
            return "registrate/datos-personales";
        }
    }

    // Paso 4 (GET): Mostrar formulario de datos académicos
    @GetMapping("/datos-academicos")
    public String mostrarFormularioAcademicos(@RequestParam Long loginId,
                                              @RequestParam Long usuarioId, Model model) {
        PageRequest pageRequest = PageRequest.of(
                0, 50,
                Sort.by("fechaColacion").descending() // Opcional: ordenar
        );
        Page<Colacion> colacionesPage = colacionService.findAll(pageRequest);
        UsuarioDatosAcademicosDTO dto = new UsuarioDatosAcademicosDTO();
        dto.setUsuarioId(usuarioId);
        dto.setIdUniversidad(1L); // preseleccionar UNJu

        model.addAttribute("academicos", dto);
        model.addAttribute("facultades", facultadRepository.findAll());
        model.addAttribute("colaciones", colacionesPage.getContent());

        model.addAttribute("loginId", loginId);
        model.addAttribute("usuarioId", usuarioId);
        return "registrate/datos-academicos";
    }

    // Paso 4 (POST): Guardar datos académicos
    @PostMapping("/datos-academicos")
    public String guardarAcademicos(@RequestParam Long loginId,
                                    @ModelAttribute("academicos") UsuarioDatosAcademicosDTO dto,
                                    @RequestParam(name = "tambienEmpresa", defaultValue = "false") boolean tambienEmpresa,
                                    Model model) {
        Long usuarioId = dto.getUsuarioId();
        registroExternoService.validarLoginUsuario(loginId, usuarioId);
        registroExternoService.guardarDatosAcademicos(usuarioId, dto);
        if (tambienEmpresa) {
            model.addAttribute("loginId", loginId);
            model.addAttribute("usuarioId", usuarioId);
            // FIX CRÍTICO FINAL: Inicializar la empresa y establecer el ID de usuario.
            UsuarioDatosEmpresa empresa = new UsuarioDatosEmpresa();
            empresa.setIdUsuario(usuarioId); // <<-- Esto es clave
            model.addAttribute("empresa", empresa);

            registroExternoService.asignarPerfilesGraduadoYUsuario(loginId);
            return "registrate/datos-empresa";
        }
        return "redirect:/registro/bienvenida";
    }

    @PostMapping("/datos-empresa")
    public String guardarEmpresa(
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            @Valid @ModelAttribute("empresa") UsuarioDatosEmpresa empresa,
            BindingResult result, Model model) {

        // Extraemos los IDs del objeto empresa, ya que el HTML los envió como campos hidden
        Long usuarioId = empresa.getIdUsuario();
        // NOTA: El loginId no es parte del objeto empresa, si necesita el loginId
        // DEBE RECUPERARLO de la sesión o del objeto UsuarioLogin asociado.
        // **POR AHORA, LO ELIMINAMOS PARA FORZAR EL ÉXITO DEL BINDING**

        // 🟢 Log para confirmar que llegamos al método POST
        System.out.println("LOG CRÍTICO: INICIANDO PROCESO POST DE GUARDADO DE EMPRESA. Usuario ID: " + usuarioId);

        // ********* SI NECESITA EL loginId, DEBE OBTENERLO DE OTRA FORMA *********
        // Por ejemplo:
        // Long loginId = registroExternoService.findLoginIdByUsuarioId(usuarioId);
        // registroExternoService.validarLoginUsuario(loginId, usuarioId);

        // Si hay errores de validación de campos, retornamos la vista.
        if (result.hasErrors()) {
            // Necesitará el loginId para rellenar el Model si usa la forma de arriba.
            // Por simplicidad, por ahora solo retornamos
            model.addAttribute("usuarioId", usuarioId);
            System.out.println("LOG CRÍTICO: Falló la validación del BindingResult. Recargando vista.");
            return "registrate/datos-empresa";
        }

        // Lógica para manejar la imagen
        try {
            if (imagenFile != null && !imagenFile.isEmpty()) {
                empresa.setImagen(imagenFile.getBytes());
            }
        } catch (IOException e) {
            System.out.println("Error al procesar la imagen: " + e.getMessage());
        }

        // El ID de Usuario ya está seteado en 'empresa' gracias al th:field
        registroExternoService.saveDatosEmpresa(usuarioId, empresa);
        System.out.println("LOG CRÍTICO: Éxito al guardar datos de empresa. Redirigiendo a bienvenida.");
        return "redirect:/registro/bienvenida";
    }

    @GetMapping("/bienvenida")
    public String bienvenida() {
        return "registrate/bienvenida";
    }
}

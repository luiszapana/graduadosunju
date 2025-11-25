package com.unju.graduados.controllers;

import com.unju.graduados.dto.AnuncioDTO;
import com.unju.graduados.model.Usuario;
import com.unju.graduados.services.IAnuncioService;
import com.unju.graduados.services.IUsuarioBaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/anuncios")
@Tag(name = "Anuncios", description = "CRUD de Anuncios")
public class AnuncioRestController {
/*
    // Dependencias
    private final IAnuncioService anuncioService;
    private final IUsuarioBaseService usuarioBaseService;

    // CONSTRUCTOR MANUAL con @Autowired y @Qualifier para resolver la ambigüedad de IUsuarioBaseService
    @Autowired
    public AnuncioRestController(IAnuncioService anuncioService,
                                 @Qualifier("usuarioBaseServiceImpl")
                                 IUsuarioBaseService usuarioBaseService) {
        this.anuncioService = anuncioService;
        this.usuarioBaseService = usuarioBaseService;
    }

    // ------------------- Métodos de Consulta (GET) -------------------

    @GetMapping
    public Page<AnuncioDTO> listar(@RequestParam(required = false) Long tipoId,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime desde,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime hasta,
                                   Pageable pageable) {
        return anuncioService.listar(tipoId, desde, hasta, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnuncioDTO> obtener(@PathVariable Long id) {
        AnuncioDTO dto = anuncioService.obtener(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    // ------------------- Método de Creación (POST) -------------------

    @PreAuthorize("hasAnyRole('ADMIN','MODERADOR')")
    @PostMapping
    public ResponseEntity<AnuncioDTO> crear(@RequestBody AnuncioDTO dto, Principal principal) {

        // Obtener el ID del usuario creador desde el Principal
        String nombreUsuario = principal.getName();
        Optional<Usuario> usuarioOpt = usuarioBaseService.findByNombreLogin(nombreUsuario);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Long idUsuarioCreador = usuarioOpt.get().getId();

        // Llamada al método unificado del Service (que ahora devuelve AnuncioDTO)
        AnuncioDTO creado = anuncioService.crear(dto, idUsuarioCreador);

        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // ------------------- Método de Actualización (PUT) -------------------

    @PreAuthorize("hasAnyRole('ADMIN','MODERADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<AnuncioDTO> actualizar(@PathVariable Long id, @RequestBody AnuncioDTO dto) {
        AnuncioDTO actualizado = anuncioService.actualizar(id, dto);
        return actualizado == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(actualizado);
    }

    // ------------------- Método de Eliminación (DELETE) -------------------

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        anuncioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

 */
}
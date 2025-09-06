package com.unju.graduados.rest;

import com.unju.graduados.dto.AnuncioDTO;
import com.unju.graduados.service.IAnuncioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/anuncios")
@RequiredArgsConstructor
@Tag(name = "Anuncios", description = "CRUD de Anuncios")
public class AnuncioRestController {

    private final IAnuncioService anuncioService;

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

    @PreAuthorize("hasAnyRole('ADMIN','MODERADOR')")
    @PostMapping
    public ResponseEntity<AnuncioDTO> crear(@RequestBody AnuncioDTO dto) {
        AnuncioDTO creado = anuncioService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<AnuncioDTO> actualizar(@PathVariable Long id, @RequestBody AnuncioDTO dto) {
        AnuncioDTO actualizado = anuncioService.actualizar(id, dto);
        return actualizado == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(actualizado);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        anuncioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

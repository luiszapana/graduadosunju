package com.unju.graduados.controllers.api;
// Ajusta el paquete

import com.unju.graduados.dto.CarreraDTO;
import java.util.List;

import com.unju.graduados.services.ICarreraService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor; // 👈 ¡Importación necesaria!

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CarreraApiController {

    private final ICarreraService carreraService;

    /**
     * Endpoint para buscar carreras, utilizado por el JavaScript del formulario.
     * URL: /api/carreras
     */
    @GetMapping("/carreras")
    public List<CarreraDTO> buscarCarreras(@RequestParam(required = false) String query,
                                           @RequestParam(required = false) Long facultadId) {
        return carreraService.buscarCarreras(query, facultadId);
    }
}

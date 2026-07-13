package com.eleuterio.abarrotes.controller;

import com.eleuterio.abarrotes.dto.*;
import com.eleuterio.abarrotes.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<PageResponse<ProductoResponse>> listar(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String categoria,
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "5") int limite
    ) {
        return ResponseEntity.ok(productoService.listar(busqueda, categoria, pagina, limite));
    }

    @GetMapping("/stats")
    public ResponseEntity<InventarioStatsResponse> stats() {
        return ResponseEntity.ok(productoService.stats());
    }

    @GetMapping("/frecuentes")
    public ResponseEntity<List<ProductoResponse>> frecuentes() {
        return ResponseEntity.ok(productoService.frecuentes());
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<AlertaResponse>> alertas() {
        return ResponseEntity.ok(productoService.alertas());
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Integer id,
                                                         @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Integer id) {
        productoService.eliminar(id);
        return ResponseEntity.ok(Map.of("message", "Producto eliminado correctamente"));
    }
}

package com.eleuterio.abarrotes.controller;

import com.eleuterio.abarrotes.dto.VentaRequest;
import com.eleuterio.abarrotes.dto.VentaResponse;
import com.eleuterio.abarrotes.security.UsuarioPrincipal;
import com.eleuterio.abarrotes.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<VentaResponse> registrar(@Valid @RequestBody VentaRequest request,
                                                     @AuthenticationPrincipal UsuarioPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ventaService.registrar(request, principal.getId()));
    }

    @GetMapping("/hoy")
    public ResponseEntity<Map<String, Object>> ventasHoy() {
        return ResponseEntity.ok(ventaService.ventasHoy());
    }

    @GetMapping("/semana")
    public ResponseEntity<Map<String, Object>> ventasSemana() {
        return ResponseEntity.ok(ventaService.ventasSemana());
    }
}

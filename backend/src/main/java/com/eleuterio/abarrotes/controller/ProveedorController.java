package com.eleuterio.abarrotes.controller;

import com.eleuterio.abarrotes.dto.OrdenProveedorRequest;
import com.eleuterio.abarrotes.entity.Proveedor;
import com.eleuterio.abarrotes.security.UsuarioPrincipal;
import com.eleuterio.abarrotes.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping("/api/proveedores")
    public ResponseEntity<List<Proveedor>> listarProveedores() {
        return ResponseEntity.ok(proveedorService.listar());
    }

    @PostMapping("/api/ordenes")
    public ResponseEntity<Map<String, Object>> registrarOrden(
            @Valid @RequestBody OrdenProveedorRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(proveedorService.registrarOrden(request, principal.getId()));
    }
}

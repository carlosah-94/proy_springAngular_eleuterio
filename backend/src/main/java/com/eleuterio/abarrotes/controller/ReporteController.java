package com.eleuterio.abarrotes.controller;

import com.eleuterio.abarrotes.service.ProveedorService;
import com.eleuterio.abarrotes.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;
    private final ProveedorService proveedorService;

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> resumen() {
        return ResponseEntity.ok(reporteService.resumen());
    }

    @GetMapping("/ventas-semana")
    public ResponseEntity<Map<String, Object>> ventasSemana() {
        return ResponseEntity.ok(reporteService.ventasSemanaDetalle());
    }

    @GetMapping("/gastos-semana")
    public ResponseEntity<Map<String, Object>> gastosSemana() {
        return ResponseEntity.ok(reporteService.gastosSemanaDetalle());
    }

    @GetMapping("/proveedores-mes")
    public ResponseEntity<List<Map<String, Object>>> proveedoresMes() {
        return ResponseEntity.ok(proveedorService.gastosMensualesPorProveedor());
    }
}

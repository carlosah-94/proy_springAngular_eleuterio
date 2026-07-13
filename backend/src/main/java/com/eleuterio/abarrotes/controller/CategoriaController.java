package com.eleuterio.abarrotes.controller;

import com.eleuterio.abarrotes.entity.Categoria;
import com.eleuterio.abarrotes.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<Categoria>> listar() {
        return ResponseEntity.ok(categoriaService.listar());
    }

    @PostMapping
    public ResponseEntity<Categoria> crear(@RequestBody Map<String, String> body) {
        String nombre = body.get("nombre");
        return ResponseEntity.ok(categoriaService.obtenerOCrear(nombre));
    }
}

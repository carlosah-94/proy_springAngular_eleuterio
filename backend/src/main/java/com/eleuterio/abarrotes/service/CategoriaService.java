package com.eleuterio.abarrotes.service;

import com.eleuterio.abarrotes.entity.Categoria;
import com.eleuterio.abarrotes.repository.CategoriaRepository;
import com.eleuterio.abarrotes.util.TextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @Transactional
    public Categoria obtenerOCrear(String nombre) {
        String norm = TextUtils.normalizar(nombre);
        return categoriaRepository.findByNombreNorm(norm)
                .orElseGet(() -> categoriaRepository.save(
                        Categoria.builder()
                                .nombre(nombre.trim())
                                .nombreNorm(norm)
                                .build()
                ));
    }
}

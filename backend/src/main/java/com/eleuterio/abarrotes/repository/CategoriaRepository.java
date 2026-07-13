package com.eleuterio.abarrotes.repository;

import com.eleuterio.abarrotes.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    Optional<Categoria> findByNombreNorm(String nombreNorm);
}

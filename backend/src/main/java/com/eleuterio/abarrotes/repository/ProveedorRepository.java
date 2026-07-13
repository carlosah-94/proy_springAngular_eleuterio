package com.eleuterio.abarrotes.repository;

import com.eleuterio.abarrotes.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    Optional<Proveedor> findByNombreNorm(String nombreNorm);
    List<Proveedor> findByActivoTrueOrderByNombreDisplayAsc();
}

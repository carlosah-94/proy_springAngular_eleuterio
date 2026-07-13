package com.eleuterio.abarrotes.repository;

import com.eleuterio.abarrotes.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    @Query("""
            SELECT p FROM Producto p
            JOIN FETCH p.categoria c
            WHERE p.activo = true
              AND (:busqueda IS NULL OR :busqueda = '' OR
                   LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR
                   LOWER(COALESCE(p.presentacion, '')) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR
                   LOWER(COALESCE(p.tipo, '')) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR
                   LOWER(c.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')))
              AND (:categoriaId IS NULL OR c.id = :categoriaId)
            ORDER BY p.nombre
            """)
    Page<Producto> buscarActivos(@Param("busqueda") String busqueda,
                                 @Param("categoriaId") Integer categoriaId,
                                 Pageable pageable);

    List<Producto> findByActivoTrueOrderByContadorVentasDesc(Pageable pageable);

    Optional<Producto> findByIdAndActivoTrue(Integer id);

    List<Producto> findByActivoTrue();
}

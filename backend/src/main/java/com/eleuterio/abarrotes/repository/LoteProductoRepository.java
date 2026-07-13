package com.eleuterio.abarrotes.repository;

import com.eleuterio.abarrotes.entity.LoteProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoteProductoRepository extends JpaRepository<LoteProducto, Integer> {

    @Query("""
            SELECT l FROM LoteProducto l
            WHERE l.producto.id = :productoId
              AND l.cantidad > 0
              AND l.fechaVenc IS NOT NULL
            ORDER BY l.fechaVenc ASC
            """)
    List<LoteProducto> findProximoVencimiento(@Param("productoId") Integer productoId);

    Optional<LoteProducto> findFirstByProductoIdAndCantidadGreaterThanAndFechaVencIsNotNullOrderByFechaVencAsc(
            Integer productoId, Integer cantidad);
}

package com.eleuterio.abarrotes.repository;

import com.eleuterio.abarrotes.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.fecha >= :inicio")
    java.math.BigDecimal sumTotalDesde(@Param("inicio") OffsetDateTime inicio);

    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fecha >= :inicio")
    long countDesde(@Param("inicio") OffsetDateTime inicio);

    List<Venta> findByFechaGreaterThanEqualOrderByFechaDesc(OffsetDateTime inicio);

    Optional<Venta> findTopByNumeroBoletaStartingWithOrderByIdDesc(String prefijo);

    @Query("""
            SELECT v FROM Venta v
            LEFT JOIN FETCH v.items i
            LEFT JOIN FETCH i.producto
            WHERE v.id = :id
            """)
    Optional<Venta> findByIdWithItems(@Param("id") Integer id);
}

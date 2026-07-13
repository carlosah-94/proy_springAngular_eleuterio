package com.eleuterio.abarrotes.repository;

import com.eleuterio.abarrotes.entity.OrdenProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface OrdenProveedorRepository extends JpaRepository<OrdenProveedor, Integer> {

    @Query("SELECT COALESCE(SUM(o.costoTotal), 0) FROM OrdenProveedor o WHERE o.fechaRecepcion >= :inicio")
    BigDecimal sumCostoDesde(@Param("inicio") LocalDate inicio);

    @Query("SELECT COUNT(o) FROM OrdenProveedor o WHERE o.fechaRecepcion >= :inicio")
    long countDesde(@Param("inicio") LocalDate inicio);

    List<OrdenProveedor> findByFechaRecepcionGreaterThanEqualOrderByFechaRecepcionAsc(LocalDate inicio);

    List<OrdenProveedor> findByFechaRecepcionGreaterThanEqual(LocalDate inicio);
}

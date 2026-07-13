package com.eleuterio.abarrotes.service;

import com.eleuterio.abarrotes.entity.OrdenProveedor;
import com.eleuterio.abarrotes.entity.Venta;
import com.eleuterio.abarrotes.repository.OrdenProveedorRepository;
import com.eleuterio.abarrotes.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private static final ZoneId LIMA = ZoneId.of("America/Lima");

    private final VentaRepository ventaRepository;
    private final OrdenProveedorRepository ordenProveedorRepository;
    private final VentaService ventaService;

    @Transactional(readOnly = true)
    public Map<String, Object> resumen() {
        OffsetDateTime inicioSemana = inicioSemana();
        LocalDate inicioSemanaDate = inicioSemana.toLocalDate();

        BigDecimal totalVentas = ventaRepository.sumTotalDesde(inicioSemana);
        long totalTransacciones = ventaRepository.countDesde(inicioSemana);
        BigDecimal totalGastos = ordenProveedorRepository.sumCostoDesde(inicioSemanaDate);
        long totalOrdenes = ordenProveedorRepository.countDesde(inicioSemanaDate);

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("totalVentas", totalVentas);
        resumen.put("totalGastos", totalGastos);
        resumen.put("totalTransacciones", totalTransacciones);
        resumen.put("totalOrdenes", totalOrdenes);
        return resumen;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> ventasSemanaDetalle() {
        OffsetDateTime inicio = inicioSemana();
        List<Venta> ventas = ventaRepository.findByFechaGreaterThanEqualOrderByFechaDesc(inicio);
        BigDecimal total = ventas.stream().map(Venta::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new HashMap<>();
        result.put("ventas", ventas.stream().map(ventaService::toResponse).toList());
        result.put("total", total);
        result.put("transacciones", ventas.size());
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> gastosSemanaDetalle() {
        LocalDate inicio = inicioSemana().toLocalDate();
        List<OrdenProveedor> ordenes = ordenProveedorRepository.findByFechaRecepcionGreaterThanEqualOrderByFechaRecepcionAsc(inicio);
        BigDecimal total = ordenes.stream()
            .map(OrdenProveedor::getCostoTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Convertir a Map para evitar problemas de serialización
        List<Map<String, Object>> ordenesDTO = ordenes.stream().map(orden -> {
        Map<String, Object> map = new HashMap<>();
            map.put("id", orden.getId());
            map.put("fechaRecepcion", orden.getFechaRecepcion());
            map.put("costoTotal", orden.getCostoTotal());
            // Agrega otros campos necesarios (ej. proveedor, descripción) sin anidar entidades
            // map.put("proveedor", orden.getProveedor().getNombre());
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("ordenes", ordenesDTO);
        result.put("total", total);
        result.put("totalOrdenes", ordenes.size());
        return result;
    }

    private OffsetDateTime inicioSemana() {
        OffsetDateTime now = OffsetDateTime.now(LIMA);
        int day = now.getDayOfWeek().getValue();
        return now.toLocalDate().minusDays(day - 1L).atStartOfDay(LIMA).toOffsetDateTime();
    }
}

package com.eleuterio.abarrotes.service;

import com.eleuterio.abarrotes.dto.VentaRequest;
import com.eleuterio.abarrotes.dto.VentaResponse;
import com.eleuterio.abarrotes.entity.*;
import com.eleuterio.abarrotes.exception.BusinessException;
import com.eleuterio.abarrotes.repository.ProductoRepository;
import com.eleuterio.abarrotes.repository.StockRepository;
import com.eleuterio.abarrotes.repository.UsuarioRepository;
import com.eleuterio.abarrotes.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VentaService {

    private static final ZoneId LIMA = ZoneId.of("America/Lima");

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final StockRepository stockRepository;

    @Transactional
    public VentaResponse registrar(VentaRequest request, Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuario no válido"));

        BigDecimal total = request.getItems().stream()
                .map(i -> i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal baseImponible = total.divide(BigDecimal.valueOf(1.18), 2, RoundingMode.HALF_UP);
        BigDecimal igv = total.subtract(baseImponible);

        String numeroBoleta = generarNumeroBoleta();

        Venta venta = Venta.builder()
                .usuario(usuario)
                .total(total.setScale(2, RoundingMode.HALF_UP))
                .baseImponible(baseImponible)
                .igv(igv.setScale(2, RoundingMode.HALF_UP))
                .numeroBoleta(numeroBoleta)
                .fecha(OffsetDateTime.now(LIMA))
                .impreso(false)
                .items(new ArrayList<>())
                .build();

        for (VentaRequest.VentaItemRequest itemReq : request.getItems()) {
            Producto producto = productoRepository.findByIdAndActivoTrue(itemReq.getProductoId())
                    .orElseThrow(() -> new BusinessException("Producto no encontrado: " + itemReq.getProductoId()));

            if (producto.getStockActual() < itemReq.getCantidad()) {
                throw new BusinessException("Stock insuficiente para " + producto.getNombre());
            }

            BigDecimal subtotal = itemReq.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(itemReq.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);

            VentaItem item = VentaItem.builder()
                    .venta(venta)
                    .producto(producto)
                    .cantidad(itemReq.getCantidad())
                    .precioUnitario(itemReq.getPrecioUnitario())
                    .subtotal(subtotal)
                    .build();
            venta.getItems().add(item);
        }

        venta = ventaRepository.save(venta);

        for (VentaItem item : venta.getItems()) {
            boolean ok = stockRepository.descontarStockFefo(item.getProducto().getId(), item.getCantidad());
            if (!ok) {
                throw new BusinessException("Error al descontar stock del producto " + item.getProducto().getNombre());
            }

            // 🔽 NUEVO: Actualizar stockActual del producto
            Producto producto = item.getProducto();
            producto.setStockActual(producto.getStockActual() - item.getCantidad());
            productoRepository.save(producto);
            // Actualizar contador de ventas (ya lo haces)
            producto.setContadorVentas(producto.getContadorVentas() + item.getCantidad());
            productoRepository.save(producto);
        }

        return toResponse(ventaRepository.findByIdWithItems(venta.getId()).orElseThrow());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> ventasHoy() {
        OffsetDateTime inicio = OffsetDateTime.now(LIMA).toLocalDate().atStartOfDay(LIMA).toOffsetDateTime();
        BigDecimal total = ventaRepository.sumTotalDesde(inicio);
        long transacciones = ventaRepository.countDesde(inicio);
        return Map.of("total", total, "transacciones", transacciones);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> ventasSemana() {
        OffsetDateTime inicio = inicioSemana();
        List<Venta> ventas = ventaRepository.findByFechaGreaterThanEqualOrderByFechaDesc(inicio);
        BigDecimal total = ventas.stream().map(Venta::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of(
                "ventas", ventas.stream().map(this::toResponse).toList(),
                "total", total,
                "transacciones", ventas.size()
        );
    }

    private String generarNumeroBoleta() {
        return ventaRepository.findTopByNumeroBoletaStartingWithOrderByIdDesc("B002-")
                .map(v -> {
                    int num = Integer.parseInt(v.getNumeroBoleta().substring(5)) + 1;
                    return "B002-" + String.format("%06d", num);
                })
                .orElse("B002-524001");
    }

    private OffsetDateTime inicioSemana() {
        OffsetDateTime now = OffsetDateTime.now(LIMA);
        int day = now.getDayOfWeek().getValue();
        return now.toLocalDate().minusDays(day - 1L).atStartOfDay(LIMA).toOffsetDateTime();
    }

    VentaResponse toResponse(Venta venta) {
        return VentaResponse.builder()
                .id(venta.getId())
                .total(venta.getTotal())
                .baseImponible(venta.getBaseImponible())
                .igv(venta.getIgv())
                .numeroBoleta(venta.getNumeroBoleta())
                .fecha(venta.getFecha())
                .items(venta.getItems().stream().map(item -> VentaResponse.VentaItemResponse.builder()
                        .cantidad(item.getCantidad())
                        .precioUnitario(item.getPrecioUnitario())
                        .subtotal(item.getSubtotal())
                        .productoNombre(item.getProducto().getNombre())
                        .productoPresentacion(item.getProducto().getPresentacion())
                        .build()).toList())
                .build();
    }
}

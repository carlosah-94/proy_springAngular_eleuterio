package com.eleuterio.abarrotes.service;

import com.eleuterio.abarrotes.dto.OrdenProveedorRequest;
import com.eleuterio.abarrotes.entity.*;
import com.eleuterio.abarrotes.exception.BusinessException;
import com.eleuterio.abarrotes.repository.*;
import com.eleuterio.abarrotes.util.TextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private static final ZoneId LIMA = ZoneId.of("America/Lima");

    private final ProveedorRepository proveedorRepository;
    private final OrdenProveedorRepository ordenProveedorRepository;
    private final ProductoRepository productoRepository;
    private final LoteProductoRepository loteProductoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Proveedor> listar() {
        return proveedorRepository.findByActivoTrueOrderByNombreDisplayAsc();
    }

    @Transactional
    public Map<String, Object> registrarOrden(OrdenProveedorRequest request, Integer usuarioId) {
        // Validar items antes de procesar
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException("Debe agregar al menos un producto a la orden.");
        }

        for (OrdenProveedorRequest.ItemOrdenRequest item : request.getItems()) {
            if (item.getCostoTotal() == null || item.getCostoTotal().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("El costo total del producto \"" + item.getNombreProducto() + "\" debe ser mayor a 0.");
            }
            if (item.getCantidadRecibida() == null || item.getCantidadRecibida() <= 0) {
                throw new BusinessException("La cantidad del producto \"" + item.getNombreProducto() + "\" debe ser mayor a 0.");
            }
        }

        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(
                () -> new BusinessException("Usuario no encontrado.")
        );

        String norm = TextUtils.normalizar(request.getNombreProveedor());
        Proveedor proveedor = proveedorRepository.findByNombreNorm(norm)
                .orElseGet(() -> proveedorRepository.save(
                        Proveedor.builder()
                                .nombreNorm(norm)
                                .nombreDisplay(request.getNombreProveedor().trim())
                                .activo(true)
                                .build()
                ));

        BigDecimal costoTotal = request.getItems().stream()
                .map(OrdenProveedorRequest.ItemOrdenRequest::getCostoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        // IMPORTANTE: Guardar la orden primero para obtener el ID (necesario para los items)
        OrdenProveedor orden = OrdenProveedor.builder()
                .proveedor(proveedor)
                .usuario(usuario)
                .fechaRecepcion(request.getFechaRecepcion())
                .costoTotal(costoTotal)
                .archivado(false)
                .build();

        orden = ordenProveedorRepository.save(orden);

        // Ahora procesar cada item con el ID de orden ya generado
        for (OrdenProveedorRequest.ItemOrdenRequest itemReq : request.getItems()) {
            // Buscar producto por ID si se proporciona, o por nombre normalizado
            Producto producto = buscarProductoPorNombre(itemReq.getNombreProducto());

            ItemOrdenProveedor item = ItemOrdenProveedor.builder()
                    .orden(orden)
                    .producto(producto)
                    .cantidadRecibida(itemReq.getCantidadRecibida())
                    .fechaVencimientoLote(itemReq.getFechaVencimientoLote())
                    .costoTotal(itemReq.getCostoTotal())
                    .build();
            orden.getItems().add(item);

            // Crear nuevo lote para este producto
            LoteProducto lote = LoteProducto.builder()
                    .producto(producto)
                    .cantidad(itemReq.getCantidadRecibida())
                    .fechaVenc(itemReq.getFechaVencimientoLote())
                    .costoUnitario(itemReq.getCostoTotal()
                            .divide(BigDecimal.valueOf(itemReq.getCantidadRecibida()), 2, RoundingMode.HALF_UP))
                    .build();
            loteProductoRepository.save(lote);

            // Actualizar stock del producto
            producto.setStockActual(producto.getStockActual() + itemReq.getCantidadRecibida());
            productoRepository.save(producto);
        }

        // Guardar la orden con los items actualizados
        ordenProveedorRepository.save(orden);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Orden registrada exitosamente");
        response.put("ordenId", orden.getId());
        return response;
    }

    /**
     * Busca un producto por nombre (acepta variaciones de mayúsculas/acentos).
     * Solo busca productos activos en el inventario.
     */
    private Producto buscarProductoPorNombre(String nombreProducto) {
        String normBusqueda = TextUtils.normalizar(nombreProducto);
        return productoRepository.findByActivoTrue().stream()
                .filter(p -> TextUtils.normalizar(p.getNombre()).equals(normBusqueda))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "El producto \"" + nombreProducto + "\" no existe en el inventario. " +
                        "Debe agregarlo primero desde el módulo Inventario."
                ));
    }

    @Transactional(readOnly = true)
    public List<OrdenProveedor> listarOrdenes() {
        LocalDate inicioSemana = OffsetDateTime.now(LIMA).toLocalDate().with(
                java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)
        );
        return ordenProveedorRepository.findByFechaRecepcionGreaterThanEqualOrderByFechaRecepcionAsc(inicioSemana);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> gastosMensualesPorProveedor() {
        LocalDate inicioMes = OffsetDateTime.now(LIMA).toLocalDate().withDayOfMonth(1);
        List<OrdenProveedor> ordenes = ordenProveedorRepository.findByFechaRecepcionGreaterThanEqual(inicioMes);

        Map<String, Map<String, Object>> map = new HashMap<>();
        for (OrdenProveedor orden : ordenes) {
            String nombre = orden.getProveedor().getNombreDisplay();
            map.computeIfAbsent(nombre, k -> {
                Map<String, Object> item = new HashMap<>();
                item.put("proveedor", nombre);
                item.put("gastoMensual", BigDecimal.ZERO);
                item.put("totalOrdenes", 0);
                item.put("categoria", "General");
                return item;
            });
            Map<String, Object> item = map.get(nombre);
            item.put("gastoMensual", ((BigDecimal) item.get("gastoMensual")).add(orden.getCostoTotal()));
            item.put("totalOrdenes", (Integer) item.get("totalOrdenes") + 1);
        }

        return map.values().stream()
                .sorted((a, b) -> ((BigDecimal) b.get("gastoMensual")).compareTo((BigDecimal) a.get("gastoMensual")))
                .toList();
    }
}

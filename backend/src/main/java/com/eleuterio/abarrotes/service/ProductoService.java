package com.eleuterio.abarrotes.service;

import com.eleuterio.abarrotes.dto.*;
import com.eleuterio.abarrotes.entity.Categoria;
import com.eleuterio.abarrotes.entity.LoteProducto;
import com.eleuterio.abarrotes.entity.Producto;
import com.eleuterio.abarrotes.exception.BusinessException;
import com.eleuterio.abarrotes.repository.CategoriaRepository;
import com.eleuterio.abarrotes.repository.LoteProductoRepository;
import com.eleuterio.abarrotes.repository.ProductoRepository;
import com.eleuterio.abarrotes.util.TextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final LoteProductoRepository loteProductoRepository;
    private final CategoriaService categoriaService;

    @Transactional(readOnly = true)
    public PageResponse<ProductoResponse> listar(String busqueda, String categoria, int pagina, int limite) {
        Integer categoriaId = null;
        if (categoria != null && !categoria.isBlank()) {
            categoriaId = categoriaRepository.findByNombreNorm(TextUtils.normalizar(categoria))
                    .map(Categoria::getId)
                    .orElse(null);
        }

        Pageable pageable = PageRequest.of(Math.max(pagina - 1, 0), limite);
        Page<Producto> page = productoRepository.buscarActivos(
                busqueda == null ? "" : busqueda.trim(),
                categoriaId,
                pageable
        );

        return PageResponse.<ProductoResponse>builder()
                .data(page.getContent().stream().map(this::toResponse).toList())
                .total(page.getTotalElements())
                .build();
    }

    @Transactional(readOnly = true)
    public InventarioStatsResponse stats() {
        List<Producto> productos = productoRepository.findByActivoTrue();
        BigDecimal valor = productos.stream()
                .map(p -> p.getPrecio().multiply(BigDecimal.valueOf(p.getStockActual())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long criticos = productos.stream()
                .filter(p -> p.getStockActual() <= p.getStockMinimo())
                .count();

        return InventarioStatsResponse.builder()
                .totalProductos(productos.size())
                .valorInventario(valor)
                .stockCritico(criticos)
                .totalCategorias(categoriaRepository.count())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> frecuentes() {
        return productoRepository.findByActivoTrueOrderByContadorVentasDesc(PageRequest.of(0, 8))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AlertaResponse> alertas() {
        List<AlertaResponse> alertas = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        for (Producto p : productoRepository.findByActivoTrue()) {
            if (p.getStockActual() == 0) {
                alertas.add(AlertaResponse.builder()
                        .tipo("error")
                        .titulo("Agotado")
                        .mensaje("\"" + p.getNombre() + "\" sin stock.")
                        .productoId(p.getId())
                        .build());
            } else if (p.getStockActual() <= p.getStockMinimo()) {
                alertas.add(AlertaResponse.builder()
                        .tipo("warning")
                        .titulo("Bajo Stock")
                        .mensaje("\"" + p.getNombre() + "\" tiene stock bajo (" + p.getStockActual() + ").")
                        .productoId(p.getId())
                        .build());
            }

            loteProductoRepository.findProximoVencimiento(p.getId()).stream().findFirst().ifPresent(lote -> {
                long dias = ChronoUnit.DAYS.between(hoy, lote.getFechaVenc());
                if (dias <= 0) {
                    alertas.add(AlertaResponse.builder()
                            .tipo("error")
                            .titulo("Vencido")
                            .mensaje("\"" + p.getNombre() + "\" ya venció (" + lote.getFechaVenc() + ").")
                            .productoId(p.getId())
                            .build());
                } else if (dias <= 30) {
                    alertas.add(AlertaResponse.builder()
                            .tipo("warning")
                            .titulo("Próximo a Vencer")
                            .mensaje("\"" + p.getNombre() + "\" vence el " + lote.getFechaVenc() + ".")
                            .productoId(p.getId())
                            .build());
                }
            });
        }

        return alertas;
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Categoria categoria = categoriaService.obtenerOCrear(request.getCategoria());
        String imagenUrl = "/assets/productos/" + TextUtils.normalizar(request.getNombre()).replace(" ", "_") + ".webp";

        Producto producto = Producto.builder()
                .nombre(request.getNombre().trim())
                .presentacion(request.getPresentacion())
                .tipo(request.getTipo())
                .precio(request.getPrecio())
                .stockActual(request.getStockInicial())
                .stockMinimo(10)
                .categoria(categoria)
                .imagenUrl(imagenUrl)
                .activo(true)
                .contadorVentas(0)
                .build();

        producto = productoRepository.save(producto);

        if (request.getStockInicial() > 0) {
            LoteProducto lote = LoteProducto.builder()
                    .producto(producto)
                    .cantidad(request.getStockInicial())
                    .fechaVenc(request.getFechaVencimiento())
                    .build();
            loteProductoRepository.save(lote);
        }

        return toResponse(producto);
    }

    @Transactional
    public ProductoResponse actualizar(Integer id, ProductoRequest request) {
        Producto producto = productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));

        if (request.getNombre() != null) producto.setNombre(request.getNombre().trim());
        if (request.getPresentacion() != null) producto.setPresentacion(request.getPresentacion());
        if (request.getTipo() != null) producto.setTipo(request.getTipo());
        if (request.getPrecio() != null) producto.setPrecio(request.getPrecio());
        if (request.getCategoria() != null) {
            producto.setCategoria(categoriaService.obtenerOCrear(request.getCategoria()));
        }

        return toResponse(productoRepository.save(producto));
    }

    @Transactional
    public void eliminar(Integer id) {
        Producto producto = productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    private ProductoResponse toResponse(Producto producto) {
        LocalDate fechaVenc = loteProductoRepository
                .findFirstByProductoIdAndCantidadGreaterThanAndFechaVencIsNotNullOrderByFechaVencAsc(producto.getId(), 0)
                .map(LoteProducto::getFechaVenc)
                .orElse(null);

        String estado;
        if (producto.getStockActual() == 0) estado = "AGOTADO";
        else if (producto.getStockActual() <= producto.getStockMinimo()) estado = "CRITICO";
        else estado = "OK";

        return ProductoResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .presentacion(producto.getPresentacion())
                .tipo(producto.getTipo())
                .precio(producto.getPrecio())
                .stockActual(producto.getStockActual())
                .stockMinimo(producto.getStockMinimo())
                .imagenUrl(producto.getImagenUrl())
                .contadorVentas(producto.getContadorVentas())
                .categoriaId(producto.getCategoria().getId())
                .categoriaNombre(producto.getCategoria().getNombre())
                .fechaVencimiento(fechaVenc)
                .estadoStock(estado)
                .build();
    }
}

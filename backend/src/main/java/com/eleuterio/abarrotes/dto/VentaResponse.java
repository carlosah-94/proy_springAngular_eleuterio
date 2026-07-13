package com.eleuterio.abarrotes.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class VentaResponse {
    private Integer id;
    private BigDecimal total;
    private BigDecimal baseImponible;
    private BigDecimal igv;
    private String numeroBoleta;
    private OffsetDateTime fecha;
    private List<VentaItemResponse> items;

    @Data
    @Builder
    public static class VentaItemResponse {
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
        private String productoNombre;
        private String productoPresentacion;
    }
}

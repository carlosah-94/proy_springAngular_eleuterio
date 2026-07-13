package com.eleuterio.abarrotes.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class InventarioStatsResponse {
    private long totalProductos;
    private BigDecimal valorInventario;
    private long stockCritico;
    private long totalCategorias;
}

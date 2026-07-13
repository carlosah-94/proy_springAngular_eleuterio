package com.eleuterio.abarrotes.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VentaRequest {
    @NotNull
    private List<VentaItemRequest> items;

    @Data
    public static class VentaItemRequest {
        @NotNull
        private Integer productoId;

        @NotNull
        @Min(1)
        private Integer cantidad;

        @NotNull
        private BigDecimal precioUnitario;
    }
}

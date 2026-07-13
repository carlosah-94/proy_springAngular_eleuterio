package com.eleuterio.abarrotes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrdenProveedorRequest {
    @NotBlank
    private String nombreProveedor;

    @NotNull
    private LocalDate fechaRecepcion;

    @NotEmpty
    private List<ItemOrdenRequest> items;

    @Data
    public static class ItemOrdenRequest {
        @NotBlank
        private String nombreProducto;

        @NotNull
        private Integer cantidadRecibida;

        private LocalDate fechaVencimientoLote;

        @NotNull
        private BigDecimal costoTotal;
    }
}

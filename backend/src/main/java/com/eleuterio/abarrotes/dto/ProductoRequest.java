package com.eleuterio.abarrotes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductoRequest {
    @NotBlank
    private String nombre;

    private String presentacion;
    private String tipo;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal precio;

    @NotNull
    @Min(0)
    @JsonProperty("stock_inicial")
    private Integer stockInicial;

    @NotBlank
    private String categoria;

    @JsonProperty("fecha_vencimiento")
    private LocalDate fechaVencimiento;
}

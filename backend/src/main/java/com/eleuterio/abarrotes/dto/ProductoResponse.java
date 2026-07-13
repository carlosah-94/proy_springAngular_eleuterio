package com.eleuterio.abarrotes.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ProductoResponse {
    private Integer id;
    private String nombre;
    private String presentacion;
    private String tipo;
    private BigDecimal precio;
    private Integer stockActual;
    private Integer stockMinimo;
    private String imagenUrl;
    private Integer contadorVentas;
    private Integer categoriaId;
    private String categoriaNombre;
    private LocalDate fechaVencimiento;
    private String estadoStock;
}

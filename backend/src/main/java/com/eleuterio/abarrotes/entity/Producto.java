package com.eleuterio.abarrotes.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(length = 100)
    private String presentacion;

    @Column(length = 100)
    private String tipo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "contador_ventas", nullable = false)
    private Integer contadorVentas;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        if (creadoEn == null) creadoEn = now;
        if (actualizadoEn == null) actualizadoEn = now;
        if (activo == null) activo = true;
        if (stockActual == null) stockActual = 0;
        if (stockMinimo == null) stockMinimo = 10;
        if (contadorVentas == null) contadorVentas = 0;
    }
}

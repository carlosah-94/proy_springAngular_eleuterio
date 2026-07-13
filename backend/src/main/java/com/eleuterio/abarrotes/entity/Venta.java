package com.eleuterio.abarrotes.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "base_imponible", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseImponible;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal igv;

    @Column(name = "numero_boleta", nullable = false, unique = true, length = 20)
    private String numeroBoleta;

    @Column(nullable = false)
    private OffsetDateTime fecha;

    @Column(nullable = false)
    private Boolean impreso;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VentaItem> items = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (fecha == null) fecha = OffsetDateTime.now();
        if (creadoEn == null) creadoEn = OffsetDateTime.now();
        if (impreso == null) impreso = false;
    }
}

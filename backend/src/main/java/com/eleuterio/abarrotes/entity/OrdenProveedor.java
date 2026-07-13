package com.eleuterio.abarrotes.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orden_proveedor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_recepcion", nullable = false)
    private LocalDate fechaRecepcion;

    @Column(name = "costo_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoTotal;

    @Column(nullable = false)
    private Boolean archivado;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemOrdenProveedor> items = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (fechaRecepcion == null) fechaRecepcion = LocalDate.now();
        if (creadoEn == null) creadoEn = OffsetDateTime.now();
        if (archivado == null) archivado = false;
    }
}

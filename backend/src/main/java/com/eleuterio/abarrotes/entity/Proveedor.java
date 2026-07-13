package com.eleuterio.abarrotes.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "proveedor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_norm", nullable = false, unique = true, length = 200)
    private String nombreNorm;

    @Column(name = "nombre_display", nullable = false, length = 200)
    private String nombreDisplay;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    @PrePersist
    void prePersist() {
        if (creadoEn == null) creadoEn = OffsetDateTime.now();
        if (activo == null) activo = true;
    }
}

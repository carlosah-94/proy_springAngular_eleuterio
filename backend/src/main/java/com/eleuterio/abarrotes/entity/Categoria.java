package com.eleuterio.abarrotes.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "nombre_norm", nullable = false, unique = true, length = 100)
    private String nombreNorm;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    @PrePersist
    void prePersist() {
        if (creadoEn == null) {
            creadoEn = OffsetDateTime.now();
        }
    }
}

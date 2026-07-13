package com.eleuterio.abarrotes.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String rol;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    @Column(name = "ultimo_acceso")
    private OffsetDateTime ultimoAcceso;

    @PrePersist
    void prePersist() {
        if (creadoEn == null) creadoEn = OffsetDateTime.now();
        if (activo == null) activo = true;
        if (rol == null) rol = "ROLE_ADMIN";
        if (nombre == null) nombre = "Don Eleuterio";
    }
}

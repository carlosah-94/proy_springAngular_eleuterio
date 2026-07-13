package com.eleuterio.abarrotes.repository;

import com.eleuterio.abarrotes.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmailIgnoreCaseAndActivoTrue(String email);
    boolean existsByEmailIgnoreCase(String email);
}

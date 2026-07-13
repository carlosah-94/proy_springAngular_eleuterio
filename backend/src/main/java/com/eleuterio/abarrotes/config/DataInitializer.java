package com.eleuterio.abarrotes.config;

import com.eleuterio.abarrotes.entity.Usuario;
import com.eleuterio.abarrotes.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.email}")
    private String seedEmail;

    @Value("${app.seed.password}")
    private String seedPassword;

    @Value("${app.seed.nombre}")
    private String seedNombre;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByEmailIgnoreCase(seedEmail)) {
            Usuario usuario = Usuario.builder()
                    .email(seedEmail.toLowerCase().trim())
                    .passwordHash(passwordEncoder.encode(seedPassword))
                    .nombre(seedNombre)
                    .rol("ROLE_ADMIN")
                    .activo(true)
                    .build();
            usuarioRepository.save(usuario);
        }
    }
}

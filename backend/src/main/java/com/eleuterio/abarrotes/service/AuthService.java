package com.eleuterio.abarrotes.service;

import com.eleuterio.abarrotes.dto.LoginRequest;
import com.eleuterio.abarrotes.dto.LoginResponse;
import com.eleuterio.abarrotes.entity.Usuario;
import com.eleuterio.abarrotes.repository.UsuarioRepository;
import com.eleuterio.abarrotes.security.JwtService;
import com.eleuterio.abarrotes.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase().trim(),
                        request.getPassword()
                )
        );

        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findById(principal.getId()).orElseThrow();
        usuario.setUltimoAcceso(OffsetDateTime.now());
        usuarioRepository.save(usuario);

        String token = jwtService.generateToken(principal);
        return LoginResponse.builder()
                .token(token)
                .nombre(principal.getNombre())
                .email(principal.getEmail())
                .rol(principal.getRol())
                .build();
    }
}

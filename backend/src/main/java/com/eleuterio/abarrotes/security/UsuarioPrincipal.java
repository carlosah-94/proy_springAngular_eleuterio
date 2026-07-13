package com.eleuterio.abarrotes.security;

import com.eleuterio.abarrotes.entity.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UsuarioPrincipal implements UserDetails {

    private final Integer id;
    private final String email;
    private final String nombre;
    private final String password;
    private final String rol;
    private final boolean activo;

    public UsuarioPrincipal(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.nombre = usuario.getNombre();
        this.password = usuario.getPasswordHash();
        this.rol = usuario.getRol();
        this.activo = Boolean.TRUE.equals(usuario.getActivo());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}

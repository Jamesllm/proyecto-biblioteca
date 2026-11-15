package com.proyecto.biblioteca.security;

import com.proyecto.biblioteca.models.Usuario;
import com.proyecto.biblioteca.repositories.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));

        String rol = usuario.getRol() != null && !usuario.getRol().isBlank()
                ? usuario.getRol()
                : "ROLE_ESTUDIANTE";

        if (!rol.startsWith("ROLE_")) {
            rol = "ROLE_" + rol;
        }

        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.isActivo(), // enabled
                true,               // accountNonExpired
                true,               // credentialsNonExpired
                true,               // accountNonLocked
                Collections.singletonList(new SimpleGrantedAuthority(rol))
        );
    }
}

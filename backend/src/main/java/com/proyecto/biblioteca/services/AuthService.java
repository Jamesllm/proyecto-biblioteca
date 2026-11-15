package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.dto.AuthResponseDTO;
import com.proyecto.biblioteca.dto.LoginRequestDTO;
import com.proyecto.biblioteca.dto.RegisterRequestDTO;
import com.proyecto.biblioteca.models.Usuario;
import com.proyecto.biblioteca.repositories.UsuarioRepository;
import com.proyecto.biblioteca.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authenticationManager,
                       UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public AuthResponseDTO login(LoginRequestDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail().trim(), loginDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication);

        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail().trim())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return AuthResponseDTO.builder()
                .token(jwt)
                .tipo("Bearer")
                .id(usuario.getId())
                .dni(usuario.getDni())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .tipoUsuario(usuario.getTipoUsuario())
                .estado(usuario.getEstado())
                .build();
    }

    public AuthResponseDTO register(RegisterRequestDTO registerDTO) {
        if (usuarioRepository.findByEmail(registerDTO.getEmail().trim()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con el email: " + registerDTO.getEmail());
        }

        if (registerDTO.getDni() != null && !registerDTO.getDni().isBlank()) {
            if (usuarioRepository.findByDni(registerDTO.getDni().trim()).isPresent()) {
                throw new IllegalArgumentException("Ya existe un usuario registrado con el DNI: " + registerDTO.getDni());
            }
        }

        Usuario usuario = new Usuario();
        usuario.setDni(registerDTO.getDni() != null ? registerDTO.getDni().trim() : null);
        usuario.setNombre(registerDTO.getNombre());
        usuario.setEmail(registerDTO.getEmail().trim().toLowerCase());
        usuario.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        usuario.setTelefono(registerDTO.getTelefono());
        usuario.setDireccion(registerDTO.getDireccion());
        usuario.setTipoUsuario(registerDTO.getTipoUsuario() != null ? registerDTO.getTipoUsuario() : "ESTUDIANTE");

        String rol = registerDTO.getRol() != null && !registerDTO.getRol().isBlank()
                ? registerDTO.getRol()
                : "ROLE_ESTUDIANTE";
        if (!rol.startsWith("ROLE_")) {
            rol = "ROLE_" + rol;
        }
        usuario.setRol(rol);
        usuario.setEstado("ACTIVO");

        Usuario guardado = usuarioRepository.save(usuario);

        // Autenticar automáticamente al nuevo usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(guardado.getEmail(), registerDTO.getPassword())
        );
        String jwt = jwtUtils.generateToken(authentication);

        return AuthResponseDTO.builder()
                .token(jwt)
                .tipo("Bearer")
                .id(guardado.getId())
                .dni(guardado.getDni())
                .nombre(guardado.getNombre())
                .email(guardado.getEmail())
                .rol(guardado.getRol())
                .tipoUsuario(guardado.getTipoUsuario())
                .estado(guardado.getEstado())
                .build();
    }

    @Transactional(readOnly = true)
    public Usuario obtenerPerfil(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
}

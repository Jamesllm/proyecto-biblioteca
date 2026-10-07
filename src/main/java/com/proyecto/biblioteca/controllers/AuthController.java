package com.proyecto.biblioteca.controllers;

import com.proyecto.biblioteca.dto.AuthResponseDTO;
import com.proyecto.biblioteca.dto.LoginRequestDTO;
import com.proyecto.biblioteca.dto.RegisterRequestDTO;
import com.proyecto.biblioteca.models.Usuario;
import com.proyecto.biblioteca.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint de inicio de sesión: valida credenciales y retorna Token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginDTO) {
        try {
            AuthResponseDTO response = authService.login(loginDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales incorrectas: Email o contraseña no válidos.");
        }
    }

    /**
     * Endpoint de registro de nuevos usuarios con contraseña encriptada en BCrypt
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerDTO) {
        try {
            AuthResponseDTO response = authService.register(registerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint para consultar el perfil del usuario autenticado actualmente con el Token JWT
     */
    @GetMapping("/me")
    public ResponseEntity<?> obtenerPerfilActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No hay una sesión autenticada activa.");
        }

        try {
            Usuario perfil = authService.obtenerPerfil(auth.getName());
            return ResponseEntity.ok(perfil);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil de usuario no encontrado.");
        }
    }
}

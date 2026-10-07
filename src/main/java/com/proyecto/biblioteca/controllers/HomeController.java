package com.proyecto.biblioteca.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> apiInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("aplicacion", "API REST - Sistema de Biblioteca Integral");
        info.put("version", "2.0.0 (JPA PostgreSQL + Spring Security JWT)");
        info.put("estado", "Activo");

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("auth", "/api/auth/login, /api/auth/register, /api/auth/me (POST/GET)");
        endpoints.put("libros", "/api/libros (GET público, POST/PUT/DELETE protegido)");
        endpoints.put("ejemplares", "/api/ejemplares (GET público, POST/PUT/DELETE protegido)");
        endpoints.put("usuarios", "/api/usuarios (CRUD, GET /{id}/prestamos, GET /{id}/prestamos/activos)");
        endpoints.put("prestamos", "/api/prestamos (GET, POST, PUT /{id}/devolver, GET /{id}/detalle)");
        endpoints.put("multas", "/api/multas (GET, POST, POST /{id}/pagar)");
        endpoints.put("pagosMultas", "/api/pagos-multas (GET, POST /multa/{idMulta})");
        endpoints.put("reservas", "/api/reservas (GET, POST, PUT /{id}/cancelar, PUT /{id}/atender)");
        endpoints.put("autores", "/api/autores (GET público, POST/PUT/DELETE protegido)");
        endpoints.put("categorias", "/api/categorias (GET público, POST/PUT/DELETE protegido)");
        endpoints.put("editoriales", "/api/editoriales (GET público, POST/PUT/DELETE protegido)");
        endpoints.put("reportes", "/api/reportes/dashboard, /api/reportes/libros-por-categoria, /api/reportes/prestamos, /api/reportes/financiero-multas");

        info.put("endpoints", endpoints);
        return info;
    }
}

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
        info.put("aplicacion", "API REST - Sistema de Biblioteca");
        info.put("estado", "Activo");
        info.put("endpoints", Map.of(
                "libros", "/api/libros (GET, POST, PUT)",
                "usuarios", "/api/usuarios (GET, POST, PUT)",
                "prestamos", "/api/prestamos (GET, POST, PUT)",
                "autores", "/api/autores (GET, POST, PUT)",
                "categorias", "/api/categorias (GET, POST, PUT)",
                "editoriales", "/api/editoriales (GET, POST, PUT)",
                "multas", "/api/multas (GET, POST, PUT)",
                "reportes", "/api/reportes/dashboard, /api/reportes/libros-por-categoria, /api/reportes/prestamos (GET)"
        ));
        return info;
    }
}

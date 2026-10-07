package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Libro;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LibroService {
    // Persistencia en memoria concurrente indexada por ISBN
    private final ConcurrentHashMap<String, Libro> libros = new ConcurrentHashMap<>();

    public LibroService() {
        // Datos semilla iniciales catalogados por ISBN
        guardar(new Libro("978-0307474728", "Cien años de soledad", "Obra cumbre del realismo mágico latinoamericano.", 1L, 1L, 1L, 1967));
        guardar(new Libro("978-8420471839", "La ciudad y los perros", "Novela ambientada en el Colegio Militar Leoncio Prado.", 2L, 1L, 2L, 1963));
        guardar(new Libro("978-0307950925", "Ficciones", "Antología de cuentos fantásticos y filosóficos.", 3L, 1L, 3L, 1944));
        guardar(new Libro("978-0307389732", "El amor en los tiempos del cólera", "Historia de amor que perdura a través de las décadas.", 1L, 1L, 1L, 1985));
        guardar(new Libro("978-8401342653", "La casa de los espíritus", "Saga familiar de los Trueba a lo largo de cuatro generaciones.", 4L, 1L, 3L, 1982));
        guardar(new Libro("978-8420471891", "Rayuela", "Novela revolucionaria de contranovela experimental.", 5L, 1L, 2L, 1963));
    }

    public List<Libro> listarTodos() {
        return new ArrayList<>(libros.values());
    }

    public Optional<Libro> buscarPorIsbn(String isbn) {
        if (isbn == null) return Optional.empty();
        return Optional.ofNullable(libros.get(isbn.trim()));
    }

    public Libro guardar(Libro libro) {
        String isbn = libro.getIsbn() != null ? libro.getIsbn().trim() : "";
        libro.setIsbn(isbn);
        libros.put(isbn, libro);
        return libro;
    }

    public Optional<Libro> actualizar(String isbn, Libro libroActualizado) {
        if (isbn == null) return Optional.empty();
        String key = isbn.trim();
        if (!libros.containsKey(key)) {
            return Optional.empty();
        }
        libroActualizado.setIsbn(key);
        libros.put(key, libroActualizado);
        return Optional.of(libroActualizado);
    }

    public boolean eliminar(String isbn) {
        if (isbn == null) return false;
        return libros.remove(isbn.trim()) != null;
    }

    public boolean existe(String isbn) {
        return isbn != null && libros.containsKey(isbn.trim());
    }
}

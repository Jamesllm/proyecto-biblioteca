package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Prestamo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PrestamoService {
    private final ConcurrentHashMap<Long, Prestamo> prestamos = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final LibroService libroService;
    private final UsuarioService usuarioService;

    public PrestamoService(LibroService libroService, UsuarioService usuarioService) {
        this.libroService = libroService;
        this.usuarioService = usuarioService;

        // Préstamos iniciales de prueba (mínimo 5 registros)
        guardar(new Prestamo(null, 4L, 1L, LocalDate.now().minusDays(5), LocalDate.now().plusDays(9), "ACTIVO"));
        libroService.actualizarDisponibilidad(4L, false);

        guardar(new Prestamo(null, 2L, 2L, LocalDate.now().minusDays(10), LocalDate.now().plusDays(4), "ACTIVO"));
        libroService.actualizarDisponibilidad(2L, false);

        guardar(new Prestamo(null, 1L, 3L, LocalDate.now().minusDays(20), LocalDate.now().minusDays(6), "DEVUELTO"));

        guardar(new Prestamo(null, 3L, 4L, LocalDate.now().minusDays(18), LocalDate.now().minusDays(4), "DEVUELTO"));

        guardar(new Prestamo(null, 5L, 5L, LocalDate.now().minusDays(16), LocalDate.now().minusDays(2), "VENCIDO"));
        libroService.actualizarDisponibilidad(5L, false);
    }

    public List<Prestamo> listarTodos() {
        return new ArrayList<>(prestamos.values());
    }

    public Optional<Prestamo> buscarPorId(Long id) {
        return Optional.ofNullable(prestamos.get(id));
    }

    public Prestamo registrarPrestamo(Prestamo prestamo) {
        if (prestamo.getIdLibro() == null || prestamo.getIdUsuario() == null) {
            throw new IllegalArgumentException("El id del libro y el id del usuario son obligatorios");
        }

        var libroOpt = libroService.buscarPorId(prestamo.getIdLibro());
        if (libroOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe el libro con ID: " + prestamo.getIdLibro());
        }

        var libro = libroOpt.get();
        if (!libro.isDisponible()) {
            throw new IllegalStateException("El libro '" + libro.getTitulo() + "' no se encuentra disponible para préstamo");
        }

        var usuarioOpt = usuarioService.buscarPorId(prestamo.getIdUsuario());
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe el usuario con ID: " + prestamo.getIdUsuario());
        }

        // Marcar libro como no disponible
        libroService.actualizarDisponibilidad(libro.getId(), false);

        // Completar fechas por defecto si no vienen
        if (prestamo.getFechaPrestamo() == null) {
            prestamo.setFechaPrestamo(LocalDate.now());
        }
        if (prestamo.getFechaDevolucion() == null) {
            prestamo.setFechaDevolucion(prestamo.getFechaPrestamo().plusDays(14));
        }
        if (prestamo.getEstado() == null || prestamo.getEstado().isBlank()) {
            prestamo.setEstado("ACTIVO");
        }

        return guardar(prestamo);
    }

    public Optional<Prestamo> actualizar(Long id, Prestamo prestamoActualizado) {
        Prestamo prestamoExistente = prestamos.get(id);
        if (prestamoExistente == null) {
            return Optional.empty();
        }

        // Si se marca como DEVUELTO y antes no lo estaba, liberamos el libro
        if ("DEVUELTO".equalsIgnoreCase(prestamoActualizado.getEstado()) && !"DEVUELTO".equalsIgnoreCase(prestamoExistente.getEstado())) {
            libroService.actualizarDisponibilidad(prestamoExistente.getIdLibro(), true);
        } else if ("ACTIVO".equalsIgnoreCase(prestamoActualizado.getEstado()) && "DEVUELTO".equalsIgnoreCase(prestamoExistente.getEstado())) {
            Long idLibro = prestamoActualizado.getIdLibro() != null ? prestamoActualizado.getIdLibro() : prestamoExistente.getIdLibro();
            libroService.actualizarDisponibilidad(idLibro, false);
        }

        prestamoActualizado.setId(id);
        if (prestamoActualizado.getIdLibro() == null) {
            prestamoActualizado.setIdLibro(prestamoExistente.getIdLibro());
        }
        if (prestamoActualizado.getIdUsuario() == null) {
            prestamoActualizado.setIdUsuario(prestamoExistente.getIdUsuario());
        }
        if (prestamoActualizado.getFechaPrestamo() == null) {
            prestamoActualizado.setFechaPrestamo(prestamoExistente.getFechaPrestamo());
        }
        if (prestamoActualizado.getFechaDevolucion() == null) {
            prestamoActualizado.setFechaDevolucion(prestamoExistente.getFechaDevolucion());
        }
        if (prestamoActualizado.getEstado() == null || prestamoActualizado.getEstado().isBlank()) {
            prestamoActualizado.setEstado(prestamoExistente.getEstado());
        }

        prestamos.put(id, prestamoActualizado);
        return Optional.of(prestamoActualizado);
    }

    private Prestamo guardar(Prestamo prestamo) {
        if (prestamo.getId() == null || prestamo.getId() <= 0) {
            prestamo.setId(idGenerator.incrementAndGet());
        } else {
            idGenerator.updateAndGet(current -> Math.max(current, prestamo.getId()));
        }
        prestamos.put(prestamo.getId(), prestamo);
        return prestamo;
    }
}

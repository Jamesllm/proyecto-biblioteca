package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Reserva;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ReservaService {
    private final ConcurrentHashMap<Long, Reserva> reservas = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    private final LibroService libroService;
    private final UsuarioService usuarioService;

    public ReservaService(LibroService libroService, UsuarioService usuarioService) {
        this.libroService = libroService;
        this.usuarioService = usuarioService;

        // Reservas iniciales de prueba
        guardar(new Reserva(null, "978-0307474728", 2L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2), "PENDIENTE", null, null));
    }

    public List<Reserva> listarTodas() {
        return reservas.values().stream()
                .map(this::enriquecerReserva)
                .collect(Collectors.toList());
    }

    public Optional<Reserva> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(reservas.get(id)).map(this::enriquecerReserva);
    }

    public List<Reserva> listarPorUsuario(Long idUsuario) {
        if (idUsuario == null) return new ArrayList<>();
        return reservas.values().stream()
                .filter(r -> idUsuario.equals(r.getIdUsuario()))
                .map(this::enriquecerReserva)
                .collect(Collectors.toList());
    }

    public List<Reserva> listarPorIsbn(String isbn) {
        if (isbn == null) return new ArrayList<>();
        return reservas.values().stream()
                .filter(r -> isbn.trim().equalsIgnoreCase(r.getIsbn()))
                .map(this::enriquecerReserva)
                .collect(Collectors.toList());
    }

    public Reserva crearReserva(Reserva reserva) {
        if (reserva.getIsbn() == null || reserva.getIdUsuario() == null) {
            throw new IllegalArgumentException("El ISBN del libro y el ID del usuario son obligatorios.");
        }

        usuarioService.buscarPorId(reserva.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("El usuario con ID " + reserva.getIdUsuario() + " no existe."));

        libroService.buscarPorIsbn(reserva.getIsbn())
                .orElseThrow(() -> new IllegalArgumentException("El libro con ISBN " + reserva.getIsbn() + " no existe."));

        if (reserva.getFechaHoraReserva() == null) {
            reserva.setFechaHoraReserva(LocalDateTime.now());
        }
        if (reserva.getFechaHoraExpiracion() == null) {
            reserva.setFechaHoraExpiracion(reserva.getFechaHoraReserva().plusDays(3));
        }
        reserva.setEstado("PENDIENTE");

        return enriquecerReserva(guardar(reserva));
    }

    public boolean cancelarReserva(Long id) {
        Reserva reserva = reservas.get(id);
        if (reserva != null) {
            reserva.setEstado("CANCELADA");
            return true;
        }
        return false;
    }

    public boolean atenderReserva(Long id) {
        Reserva reserva = reservas.get(id);
        if (reserva != null) {
            reserva.setEstado("ATENDIDA");
            return true;
        }
        return false;
    }

    public boolean eliminar(Long id) {
        if (id == null) return false;
        return reservas.remove(id) != null;
    }

    private Reserva guardar(Reserva reserva) {
        if (reserva.getId() == null || reserva.getId() <= 0) {
            reserva.setId(idGenerator.incrementAndGet());
        } else {
            idGenerator.updateAndGet(current -> Math.max(current, reserva.getId()));
        }
        reservas.put(reserva.getId(), reserva);
        return reserva;
    }

    private Reserva enriquecerReserva(Reserva r) {
        if (r == null) return null;
        if (r.getIsbn() != null) {
            libroService.buscarPorIsbn(r.getIsbn()).ifPresent(r::setLibro);
        }
        if (r.getIdUsuario() != null) {
            usuarioService.buscarPorId(r.getIdUsuario()).ifPresent(r::setUsuario);
        }
        return r;
    }
}

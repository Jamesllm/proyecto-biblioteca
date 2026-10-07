package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Multa;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class MultaService {
    private final ConcurrentHashMap<Long, Multa> multas = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public MultaService() {
        // Multas iniciales de prueba con fecha y hora
        LocalDateTime ahora = LocalDateTime.now();
        guardar(new Multa(null, 1L, 1L, "RETRASO", 72L, 3, 15.00, "Entrega con retraso de 3 días", ahora.minusDays(5), "PENDIENTE"));
        guardar(new Multa(null, 2L, 2L, "DAÑO", 0L, 0, 25.50, "Daño menor en cubierta de libro", ahora.minusDays(3), "PENDIENTE"));
        guardar(new Multa(null, 3L, 3L, "RETRASO", 48L, 2, 10.00, "Retraso de 2 días en devolución", ahora.minusDays(10), "PAGADA"));
        guardar(new Multa(null, 4L, 4L, "EXTRAVIO", 0L, 0, 30.00, "Extravío temporal de material", ahora.minusDays(15), "PAGADA"));
        guardar(new Multa(null, 5L, 5L, "RETRASO", 96L, 4, 20.00, "Retraso de 4 días en fecha límite", ahora.minusDays(2), "PENDIENTE"));
    }

    public List<Multa> listarTodas() {
        return new ArrayList<>(multas.values());
    }

    public Optional<Multa> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(multas.get(id));
    }

    public List<Multa> listarPorUsuario(Long idUsuario) {
        if (idUsuario == null) return new ArrayList<>();
        return multas.values().stream()
                .filter(m -> idUsuario.equals(m.getIdUsuario()))
                .collect(Collectors.toList());
    }

    public List<Multa> listarPendientesPorUsuario(Long idUsuario) {
        if (idUsuario == null) return new ArrayList<>();
        return multas.values().stream()
                .filter(m -> idUsuario.equals(m.getIdUsuario()) && "PENDIENTE".equalsIgnoreCase(m.getEstado()))
                .collect(Collectors.toList());
    }

    public Optional<Multa> buscarPorPrestamo(Long idPrestamo) {
        if (idPrestamo == null) return Optional.empty();
        return multas.values().stream()
                .filter(m -> idPrestamo.equals(m.getIdPrestamo()))
                .findFirst();
    }

    public Multa generarMultaPorRetraso(Long idPrestamo, Long idUsuario, long horasRetraso, int diasRetraso, double monto) {
        Multa multa = new Multa();
        multa.setIdPrestamo(idPrestamo);
        multa.setIdUsuario(idUsuario);
        multa.setTipoMulta("RETRASO");
        multa.setHorasRetraso(horasRetraso);
        multa.setDiasRetraso(diasRetraso);
        multa.setMonto(monto);
        multa.setMotivo("Retraso en devolución de " + diasRetraso + " día(s) (" + horasRetraso + " hrs)");
        multa.setFechaHoraEmision(LocalDateTime.now());
        multa.setEstado("PENDIENTE");
        return guardar(multa);
    }

    public Multa generarMultaPorDano(Long idPrestamo, Long idUsuario, double monto, String motivo) {
        Multa multa = new Multa();
        multa.setIdPrestamo(idPrestamo);
        multa.setIdUsuario(idUsuario);
        multa.setTipoMulta("DAÑO");
        multa.setMonto(monto);
        multa.setMotivo(motivo != null && !motivo.isBlank() ? motivo : "Deterioro/Daño en ejemplar retornado");
        multa.setFechaHoraEmision(LocalDateTime.now());
        multa.setEstado("PENDIENTE");
        return guardar(multa);
    }

    public Multa registrarMulta(Multa multa) {
        if (multa.getIdUsuario() == null) {
            throw new IllegalArgumentException("El id del usuario es obligatorio para registrar una multa");
        }
        if (multa.getMonto() == null || multa.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto de la multa debe ser mayor a 0");
        }
        if (multa.getFechaHoraEmision() == null) {
            multa.setFechaHoraEmision(LocalDateTime.now());
        }
        if (multa.getEstado() == null || multa.getEstado().isBlank()) {
            multa.setEstado("PENDIENTE");
        }
        return guardar(multa);
    }

    public Optional<Multa> actualizar(Long id, Multa multaActualizada) {
        if (id == null || !multas.containsKey(id)) {
            return Optional.empty();
        }
        multaActualizada.setId(id);
        multas.put(id, multaActualizada);
        return Optional.of(multaActualizada);
    }

    public boolean marcarComoPagada(Long id) {
        Multa multa = multas.get(id);
        if (multa != null) {
            multa.setEstado("PAGADA");
            return true;
        }
        return false;
    }

    public boolean anularMulta(Long id) {
        Multa multa = multas.get(id);
        if (multa != null) {
            multa.setEstado("ANULADA");
            return true;
        }
        return false;
    }

    public boolean eliminar(Long id) {
        if (id == null) return false;
        return multas.remove(id) != null;
    }

    public Multa guardar(Multa multa) {
        if (multa.getId() == null || multa.getId() <= 0) {
            multa.setId(idGenerator.incrementAndGet());
        } else {
            idGenerator.updateAndGet(current -> Math.max(current, multa.getId()));
        }
        multas.put(multa.getId(), multa);
        return multa;
    }
}

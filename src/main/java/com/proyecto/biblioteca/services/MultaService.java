package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Multa;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MultaService {
    private final ConcurrentHashMap<Long, Multa> multas = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public MultaService() {
        // Multas iniciales de prueba (mínimo 5 registros)
        guardar(new Multa(null, 1L, 1L, 15.00, "Entrega con retraso de 3 días", false));
        guardar(new Multa(null, 2L, 2L, 25.50, "Daño menor en cubierta de libro", false));
        guardar(new Multa(null, 3L, 3L, 10.00, "Retraso de 2 días en devolución", true));
        guardar(new Multa(null, 4L, 4L, 30.00, "Extravío temporal de material", true));
        guardar(new Multa(null, 5L, 5L, 20.00, "Retraso de 4 días en fecha límite", false));
    }

    public List<Multa> listarTodas() {
        return new ArrayList<>(multas.values());
    }

    public Optional<Multa> buscarPorId(Long id) {
        return Optional.ofNullable(multas.get(id));
    }

    public Multa registrarMulta(Multa multa) {
        if (multa.getIdUsuario() == null) {
            throw new IllegalArgumentException("El id del usuario es obligatorio para registrar una multa");
        }
        if (multa.getMonto() == null || multa.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto de la multa debe ser mayor a 0");
        }
        if (multa.getPagada() == null) {
            multa.setPagada(false);
        }
        return guardar(multa);
    }

    public Optional<Multa> actualizar(Long id, Multa multaActualizada) {
        Multa multaExistente = multas.get(id);
        if (multaExistente == null) {
            return Optional.empty();
        }
        multaActualizada.setId(id);
        if (multaActualizada.getIdPrestamo() == null) {
            multaActualizada.setIdPrestamo(multaExistente.getIdPrestamo());
        }
        if (multaActualizada.getIdUsuario() == null) {
            multaActualizada.setIdUsuario(multaExistente.getIdUsuario());
        }
        if (multaActualizada.getMonto() == null) {
            multaActualizada.setMonto(multaExistente.getMonto());
        }
        if (multaActualizada.getMotivo() == null) {
            multaActualizada.setMotivo(multaExistente.getMotivo());
        }
        if (multaActualizada.getPagada() == null) {
            multaActualizada.setPagada(multaExistente.isPagada());
        }
        multas.put(id, multaActualizada);
        return Optional.of(multaActualizada);
    }

    private Multa guardar(Multa multa) {
        if (multa.getId() == null || multa.getId() <= 0) {
            multa.setId(idGenerator.incrementAndGet());
        } else {
            idGenerator.updateAndGet(current -> Math.max(current, multa.getId()));
        }
        multas.put(multa.getId(), multa);
        return multa;
    }
}

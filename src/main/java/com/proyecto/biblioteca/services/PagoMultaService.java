package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.dto.PagoMultaRequestDTO;
import com.proyecto.biblioteca.models.Multa;
import com.proyecto.biblioteca.models.PagoMulta;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class PagoMultaService {
    private final ConcurrentHashMap<Long, PagoMulta> pagos = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    private final MultaService multaService;
    private final UsuarioService usuarioService;

    public PagoMultaService(MultaService multaService, UsuarioService usuarioService) {
        this.multaService = multaService;
        this.usuarioService = usuarioService;

        // Datos semilla iniciales de pagos realizados
        guardar(new PagoMulta(null, 3L, 10.00, LocalDateTime.now().minusDays(10), "EFECTIVO", "REC-00101"));
        guardar(new PagoMulta(null, 4L, 30.00, LocalDateTime.now().minusDays(15), "TARJETA", "REC-00102"));
    }

    public List<PagoMulta> listarTodos() {
        return new ArrayList<>(pagos.values());
    }

    public Optional<PagoMulta> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(pagos.get(id));
    }

    public List<PagoMulta> listarPorMulta(Long idMulta) {
        if (idMulta == null) return new ArrayList<>();
        return pagos.values().stream()
                .filter(p -> idMulta.equals(p.getIdMulta()))
                .collect(Collectors.toList());
    }

    public PagoMulta procesarPago(Long idMulta, PagoMultaRequestDTO dto) {
        Multa multa = multaService.buscarPorId(idMulta)
                .orElseThrow(() -> new IllegalArgumentException("La multa con ID " + idMulta + " no existe."));

        if ("PAGADA".equalsIgnoreCase(multa.getEstado())) {
            throw new IllegalStateException("La multa con ID " + idMulta + " ya ha sido pagada previamente.");
        }

        if (dto.getMontoPagado() == null || dto.getMontoPagado() < multa.getMonto()) {
            throw new IllegalArgumentException("El monto pagado (" + dto.getMontoPagado() + ") no puede ser menor al monto de la multa (" + multa.getMonto() + ").");
        }

        // Marcar la multa como PAGADA
        multaService.marcarComoPagada(idMulta);

        // Crear registro de pago
        PagoMulta pago = new PagoMulta();
        pago.setIdMulta(idMulta);
        pago.setMontoPagado(dto.getMontoPagado());
        pago.setFechaHoraPago(LocalDateTime.now());
        pago.setMetodoPago(dto.getMetodoPago() != null ? dto.getMetodoPago() : "EFECTIVO");
        
        String comprobante = dto.getComprobante();
        if (comprobante == null || comprobante.isBlank()) {
            comprobante = "BOL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        pago.setComprobante(comprobante);

        PagoMulta pagoGuardado = guardar(pago);

        // Verificar si el usuario ya no tiene multas pendientes para reactivarlo a ACTIVO
        Long idUsuario = multa.getIdUsuario();
        List<Multa> pendientes = multaService.listarPendientesPorUsuario(idUsuario);
        if (pendientes.isEmpty()) {
            usuarioService.activarUsuario(idUsuario);
        }

        return pagoGuardado;
    }

    public PagoMulta guardar(PagoMulta pago) {
        if (pago.getId() == null || pago.getId() <= 0) {
            pago.setId(idGenerator.incrementAndGet());
        } else {
            idGenerator.updateAndGet(current -> Math.max(current, pago.getId()));
        }
        pagos.put(pago.getId(), pago);
        return pago;
    }
}

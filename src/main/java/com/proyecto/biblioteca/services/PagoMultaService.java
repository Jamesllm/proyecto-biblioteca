package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.dto.PagoMultaRequestDTO;
import com.proyecto.biblioteca.models.Multa;
import com.proyecto.biblioteca.models.PagoMulta;
import com.proyecto.biblioteca.repositories.PagoMultaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PagoMultaService {

    private final PagoMultaRepository pagoMultaRepository;
    private final MultaService multaService;
    private final UsuarioService usuarioService;

    public PagoMultaService(PagoMultaRepository pagoMultaRepository,
                             MultaService multaService,
                             UsuarioService usuarioService) {
        this.pagoMultaRepository = pagoMultaRepository;
        this.multaService = multaService;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<PagoMulta> listarTodos() {
        return pagoMultaRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<PagoMulta> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return pagoMultaRepository.findByIdAndActivoTrue(id);
    }

    @Transactional(readOnly = true)
    public List<PagoMulta> listarPorMulta(Long idMulta) {
        if (idMulta == null) return List.of();
        return pagoMultaRepository.findByIdMultaAndActivoTrue(idMulta);
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

        // 1. Marcar multa como PAGADA
        multaService.marcarComoPagada(idMulta);

        // 2. Crear y persistir el registro de PagoMulta
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
        pago.setActivo(true);

        PagoMulta guardado = pagoMultaRepository.save(pago);

        // 3. Si el usuario no tiene más multas pendientes, reactivarlo a ACTIVO
        Long idUsuario = multa.getIdUsuario();
        List<Multa> pendientes = multaService.listarPendientesPorUsuario(idUsuario);
        if (pendientes.isEmpty()) {
            usuarioService.activarUsuario(idUsuario);
        }

        return guardado;
    }

    public boolean eliminar(Long id) {
        if (id == null) return false;
        return pagoMultaRepository.findByIdAndActivoTrue(id).map(p -> {
            p.setActivo(false);
            pagoMultaRepository.save(p);
            return true;
        }).orElse(false);
    }

    public PagoMulta guardar(PagoMulta pago) {
        if (pago.getActivo() == null) {
            pago.setActivo(true);
        }
        return pagoMultaRepository.save(pago);
    }
}

package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.dto.DevolucionRequestDTO;
import com.proyecto.biblioteca.dto.PrestamoDetalladoDTO;
import com.proyecto.biblioteca.models.Ejemplar;
import com.proyecto.biblioteca.models.Libro;
import com.proyecto.biblioteca.models.Multa;
import com.proyecto.biblioteca.models.Prestamo;
import com.proyecto.biblioteca.models.Usuario;
import com.proyecto.biblioteca.repositories.PrestamoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PrestamoService {
    public static final double TARIFA_DIARIA_MORA = 2.50; // S/. 2.50 por día de retraso

    private final PrestamoRepository prestamoRepository;
    private final EjemplarService ejemplarService;
    private final LibroService libroService;
    private final UsuarioService usuarioService;
    private final MultaService multaService;

    public PrestamoService(PrestamoRepository prestamoRepository,
                           EjemplarService ejemplarService,
                           LibroService libroService,
                           UsuarioService usuarioService,
                           MultaService multaService) {
        this.prestamoRepository = prestamoRepository;
        this.ejemplarService = ejemplarService;
        this.libroService = libroService;
        this.usuarioService = usuarioService;
        this.multaService = multaService;
    }

    @Transactional(readOnly = true)
    public List<Prestamo> listarTodos() {
        return prestamoRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Prestamo> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return prestamoRepository.findByIdAndActivoTrue(id);
    }

    @Transactional(readOnly = true)
    public List<Prestamo> listarPorUsuario(Long idUsuario) {
        if (idUsuario == null) return List.of();
        return prestamoRepository.findByIdUsuarioAndActivoTrue(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Prestamo> listarActivosPorUsuario(Long idUsuario) {
        if (idUsuario == null) return List.of();
        return prestamoRepository.findByIdUsuarioAndEstadoInAndActivoTrue(idUsuario, List.of("ACTIVO", "VENCIDO"));
    }

    public Prestamo registrarPrestamo(Prestamo prestamo) {
        if (prestamo.getIdEjemplar() == null || prestamo.getIdUsuario() == null) {
            throw new IllegalArgumentException("El ID del ejemplar físico y el ID del usuario son obligatorios.");
        }

        // 1. Validar usuario existente y no sancionado
        Usuario usuario = usuarioService.buscarPorId(prestamo.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario con ID: " + prestamo.getIdUsuario()));

        if (!usuario.isActivo()) {
            throw new IllegalStateException("El usuario '" + usuario.getNombre() + "' se encuentra " + usuario.getEstado() + " y no puede solicitar préstamos.");
        }

        // 2. Validar ejemplar existente y disponible
        Ejemplar ejemplar = ejemplarService.buscarPorId(prestamo.getIdEjemplar())
                .orElseThrow(() -> new IllegalArgumentException("No existe el ejemplar físico con ID: " + prestamo.getIdEjemplar()));

        if (!ejemplar.isDisponible()) {
            throw new IllegalStateException("El ejemplar (Copia #" + ejemplar.getNumeroCopia() + " de '" + ejemplar.getIsbn() + "') se encuentra '" + ejemplar.getEstado() + "' y no está disponible.");
        }

        // 3. Configurar fechas exactas con LocalDateTime
        LocalDateTime ahora = LocalDateTime.now();
        if (prestamo.getFechaHoraPrestamo() == null) {
            prestamo.setFechaHoraPrestamo(ahora);
        }
        if (prestamo.getFechaHoraDevolucionEsperada() == null) {
            prestamo.setFechaHoraDevolucionEsperada(prestamo.getFechaHoraPrestamo().plusDays(14));
        }
        prestamo.setEstado("ACTIVO");
        prestamo.setFechaHoraDevolucionReal(null);

        // 4. Cambiar estado de ejemplar a PRESTADO
        ejemplarService.cambiarEstado(ejemplar.getId(), "PRESTADO");

        return prestamoRepository.save(prestamo);
    }

    public Prestamo registrarDevolucion(Long idPrestamo, DevolucionRequestDTO dto) {
        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new IllegalArgumentException("No existe el préstamo con ID: " + idPrestamo));

        if ("DEVUELTO".equalsIgnoreCase(prestamo.getEstado())) {
            throw new IllegalStateException("El préstamo con ID " + idPrestamo + " ya fue devuelto con anterioridad.");
        }

        LocalDateTime fechaEntregaReal = (dto != null && dto.getFechaHoraDevolucionReal() != null)
                ? dto.getFechaHoraDevolucionReal()
                : LocalDateTime.now();

        prestamo.setFechaHoraDevolucionReal(fechaEntregaReal);
        prestamo.setEstado("DEVUELTO");

        // 1. Evaluar mora por exceso de tiempo
        if (fechaEntregaReal.isAfter(prestamo.getFechaHoraDevolucionEsperada())) {
            Duration duracionMora = Duration.between(prestamo.getFechaHoraDevolucionEsperada(), fechaEntregaReal);
            long horasRetraso = duracionMora.toHours();
            int diasRetraso = Math.max(1, (int) Math.ceil((double) horasRetraso / 24.0));
            double montoMora = diasRetraso * TARIFA_DIARIA_MORA;

            multaService.generarMultaPorRetraso(prestamo.getId(), prestamo.getIdUsuario(), horasRetraso, diasRetraso, montoMora);
            usuarioService.sancionarUsuario(prestamo.getIdUsuario());
        }

        // 2. Evaluar daño físico si aplica
        if (dto != null && dto.getMontoDanoExtra() != null && dto.getMontoDanoExtra() > 0) {
            multaService.generarMultaPorDano(prestamo.getId(), prestamo.getIdUsuario(), dto.getMontoDanoExtra(), dto.getObservaciones());
            usuarioService.sancionarUsuario(prestamo.getIdUsuario());
        }

        // 3. Retornar ejemplar al inventario
        String estadoConservacion = (dto != null && dto.getEstadoConservacion() != null && !dto.getEstadoConservacion().isBlank())
                ? dto.getEstadoConservacion()
                : "BUENO";

        ejemplarService.cambiarEstadoConservacion(prestamo.getIdEjemplar(), estadoConservacion);
        if ("DETERIORADO".equalsIgnoreCase(estadoConservacion) || "DAÑADO".equalsIgnoreCase(estadoConservacion)) {
            ejemplarService.cambiarEstado(prestamo.getIdEjemplar(), "EN_MANTENIMIENTO");
        } else {
            ejemplarService.cambiarEstado(prestamo.getIdEjemplar(), "DISPONIBLE");
        }

        return prestamoRepository.save(prestamo);
    }

    @Transactional(readOnly = true)
    public List<PrestamoDetalladoDTO> obtenerDetallePrestamosPorUsuario(Long idUsuario, boolean soloActivos) {
        List<Prestamo> lista = soloActivos ? listarActivosPorUsuario(idUsuario) : listarPorUsuario(idUsuario);
        return lista.stream()
                .map(this::convertirADetalleDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<PrestamoDetalladoDTO> obtenerDetallePrestamo(Long id) {
        return buscarPorId(id).map(this::convertirADetalleDTO);
    }

    public Optional<Prestamo> actualizar(Long id, Prestamo prestamoActualizado) {
        if (id == null) return Optional.empty();
        return prestamoRepository.findByIdAndActivoTrue(id).map(existente -> {
            existente.setEstado(prestamoActualizado.getEstado());
            existente.setFechaHoraDevolucionEsperada(prestamoActualizado.getFechaHoraDevolucionEsperada());
            existente.setFechaHoraDevolucionReal(prestamoActualizado.getFechaHoraDevolucionReal());
            return prestamoRepository.save(existente);
        });
    }

    public boolean eliminar(Long id) {
        if (id == null) return false;
        return prestamoRepository.findByIdAndActivoTrue(id).map(p -> {
            if ("ACTIVO".equalsIgnoreCase(p.getEstado())) {
                ejemplarService.cambiarEstado(p.getIdEjemplar(), "DISPONIBLE");
            }
            p.setActivo(false);
            prestamoRepository.save(p);
            return true;
        }).orElse(false);
    }

    public Prestamo guardar(Prestamo prestamo) {
        if (prestamo.getActivo() == null) {
            prestamo.setActivo(true);
        }
        return prestamoRepository.save(prestamo);
    }

    private PrestamoDetalladoDTO convertirADetalleDTO(Prestamo p) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaLimite = p.getFechaHoraDevolucionEsperada();
        LocalDateTime fechaReal = p.getFechaHoraDevolucionReal();

        boolean enMora = false;
        long horasRetraso = 0;
        int diasRetraso = 0;
        long horasRestantes = 0;

        if (fechaReal != null) {
            if (fechaReal.isAfter(fechaLimite)) {
                enMora = true;
                horasRetraso = Duration.between(fechaLimite, fechaReal).toHours();
                diasRetraso = Math.max(1, (int) Math.ceil((double) horasRetraso / 24.0));
            }
        } else {
            if (ahora.isAfter(fechaLimite)) {
                enMora = true;
                horasRetraso = Duration.between(fechaLimite, ahora).toHours();
                diasRetraso = Math.max(1, (int) Math.ceil((double) horasRetraso / 24.0));
            } else {
                horasRestantes = Duration.between(ahora, fechaLimite).toHours();
            }
        }

        Ejemplar ejemplar = p.getEjemplar();
        if (ejemplar == null && p.getIdEjemplar() != null) {
            ejemplar = ejemplarService.buscarPorId(p.getIdEjemplar()).orElse(null);
        }

        Libro libro = null;
        if (ejemplar != null) {
            libro = ejemplar.getLibro();
            if (libro == null && ejemplar.getIsbn() != null) {
                libro = libroService.buscarPorIsbn(ejemplar.getIsbn()).orElse(null);
            }
        }

        Usuario usuario = p.getUsuario();
        if (usuario == null && p.getIdUsuario() != null) {
            usuario = usuarioService.buscarPorId(p.getIdUsuario()).orElse(null);
        }

        Multa multa = multaService.buscarPorPrestamo(p.getId()).orElse(null);

        return PrestamoDetalladoDTO.builder()
                .idPrestamo(p.getId())
                .estadoPrestamo(p.getEstado())
                .fechaHoraPrestamo(p.getFechaHoraPrestamo())
                .fechaHoraDevolucionEsperada(p.getFechaHoraDevolucionEsperada())
                .fechaHoraDevolucionReal(p.getFechaHoraDevolucionReal())
                .enMora(enMora)
                .horasRetraso(horasRetraso)
                .diasRetraso(diasRetraso)
                .horasRestantes(horasRestantes)
                .usuario(usuario)
                .ejemplar(ejemplar)
                .libro(libro)
                .multa(multa)
                .build();
    }
}

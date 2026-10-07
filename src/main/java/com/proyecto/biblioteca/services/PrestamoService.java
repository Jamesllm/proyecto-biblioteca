package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.dto.DevolucionRequestDTO;
import com.proyecto.biblioteca.dto.PrestamoDetalladoDTO;
import com.proyecto.biblioteca.models.Ejemplar;
import com.proyecto.biblioteca.models.Libro;
import com.proyecto.biblioteca.models.Multa;
import com.proyecto.biblioteca.models.Prestamo;
import com.proyecto.biblioteca.models.Usuario;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class PrestamoService {
    public static final double TARIFA_DIARIA_MORA = 2.50; // S/. 2.50 por día de retraso

    private final ConcurrentHashMap<Long, Prestamo> prestamos = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    private final EjemplarService ejemplarService;
    private final LibroService libroService;
    private final UsuarioService usuarioService;
    private final MultaService multaService;

    public PrestamoService(EjemplarService ejemplarService, LibroService libroService,
                           UsuarioService usuarioService, MultaService multaService) {
        this.ejemplarService = ejemplarService;
        this.libroService = libroService;
        this.usuarioService = usuarioService;
        this.multaService = multaService;

        // Datos iniciales de prueba con LocalDateTime
        LocalDateTime ahora = LocalDateTime.now();

        // 1. Prestamo activo normal
        guardar(new Prestamo(null, 1L, 1L, ahora.minusDays(5), ahora.plusDays(9), null, "ACTIVO", null, null));
        ejemplarService.cambiarEstado(1L, "PRESTADO");

        // 2. Prestamo activo por vencer
        guardar(new Prestamo(null, 3L, 2L, ahora.minusDays(10), ahora.plusDays(4), null, "ACTIVO", null, null));
        ejemplarService.cambiarEstado(3L, "PRESTADO");

        // 3. Prestamo devuelto a tiempo
        guardar(new Prestamo(null, 5L, 3L, ahora.minusDays(20), ahora.minusDays(6), ahora.minusDays(7), "DEVUELTO", null, null));

        // 4. Prestamo devuelto con retraso
        guardar(new Prestamo(null, 6L, 4L, ahora.minusDays(18), ahora.minusDays(10), ahora.minusDays(5), "DEVUELTO", null, null));

        // 5. Prestamo vencido (en mora sin devolver)
        guardar(new Prestamo(null, 7L, 5L, ahora.minusDays(16), ahora.minusDays(2), null, "VENCIDO", null, null));
        ejemplarService.cambiarEstado(7L, "PRESTADO");
    }

    public List<Prestamo> listarTodos() {
        return prestamos.values().stream()
                .map(this::enriquecerPrestamo)
                .collect(Collectors.toList());
    }

    public Optional<Prestamo> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(prestamos.get(id)).map(this::enriquecerPrestamo);
    }

    public List<Prestamo> listarPorUsuario(Long idUsuario) {
        if (idUsuario == null) return new ArrayList<>();
        return prestamos.values().stream()
                .filter(p -> idUsuario.equals(p.getIdUsuario()))
                .map(this::enriquecerPrestamo)
                .collect(Collectors.toList());
    }

    public List<Prestamo> listarActivosPorUsuario(Long idUsuario) {
        if (idUsuario == null) return new ArrayList<>();
        return prestamos.values().stream()
                .filter(p -> idUsuario.equals(p.getIdUsuario()) && ("ACTIVO".equalsIgnoreCase(p.getEstado()) || "VENCIDO".equalsIgnoreCase(p.getEstado())))
                .map(this::enriquecerPrestamo)
                .collect(Collectors.toList());
    }

    public Prestamo registrarPrestamo(Prestamo prestamo) {
        if (prestamo.getIdEjemplar() == null || prestamo.getIdUsuario() == null) {
            throw new IllegalArgumentException("El id del ejemplar físico y el id del usuario son obligatorios.");
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
            // Regla: 14 días calendario por defecto
            prestamo.setFechaHoraDevolucionEsperada(prestamo.getFechaHoraPrestamo().plusDays(14));
        }
        prestamo.setEstado("ACTIVO");
        prestamo.setFechaHoraDevolucionReal(null);

        // 4. Conmutar estado de ejemplar a PRESTADO
        ejemplarService.cambiarEstado(ejemplar.getId(), "PRESTADO");

        Prestamo guardado = guardar(prestamo);
        return enriquecerPrestamo(guardado);
    }

    /**
     * Registra la devolución física de un préstamo evaluando fechas reales, posibles moras o daños.
     */
    public Prestamo registrarDevolucion(Long idPrestamo, DevolucionRequestDTO dto) {
        Prestamo prestamo = prestamos.get(idPrestamo);
        if (prestamo == null) {
            throw new IllegalArgumentException("No existe el préstamo con ID: " + idPrestamo);
        }

        if ("DEVUELTO".equalsIgnoreCase(prestamo.getEstado())) {
            throw new IllegalStateException("El préstamo con ID " + idPrestamo + " ya fue devuelto con anterioridad.");
        }

        LocalDateTime fechaEntregaReal = (dto != null && dto.getFechaHoraDevolucionReal() != null)
                ? dto.getFechaHoraDevolucionReal()
                : LocalDateTime.now();

        prestamo.setFechaHoraDevolucionReal(fechaEntregaReal);
        prestamo.setEstado("DEVUELTO");

        // 1. Evaluar si hubo exceso de tiempo respecto a la fecha esperada
        if (fechaEntregaReal.isAfter(prestamo.getFechaHoraDevolucionEsperada())) {
            Duration duracionMora = Duration.between(prestamo.getFechaHoraDevolucionEsperada(), fechaEntregaReal);
            long horasRetraso = duracionMora.toHours();
            int diasRetraso = Math.max(1, (int) Math.ceil((double) horasRetraso / 24.0));
            double montoMora = diasRetraso * TARIFA_DIARIA_MORA;

            // Generar multa automática por mora
            multaService.generarMultaPorRetraso(prestamo.getId(), prestamo.getIdUsuario(), horasRetraso, diasRetraso, montoMora);

            // Sancionar al usuario hasta que pague
            usuarioService.sancionarUsuario(prestamo.getIdUsuario());
        }

        // 2. Evaluar daño físico reportado si aplica
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

        prestamos.put(prestamo.getId(), prestamo);
        return enriquecerPrestamo(prestamo);
    }

    /**
     * Consulta enriquecida y detallada de préstamos de un usuario
     */
    public List<PrestamoDetalladoDTO> obtenerDetallePrestamosPorUsuario(Long idUsuario, boolean soloActivos) {
        List<Prestamo> lista = soloActivos ? listarActivosPorUsuario(idUsuario) : listarPorUsuario(idUsuario);
        return lista.stream()
                .map(this::convertirADetalleDTO)
                .collect(Collectors.toList());
    }

    public Optional<PrestamoDetalladoDTO> obtenerDetallePrestamo(Long id) {
        return buscarPorId(id).map(this::convertirADetalleDTO);
    }

    public Optional<Prestamo> actualizar(Long id, Prestamo prestamoActualizado) {
        Prestamo prestamoExistente = prestamos.get(id);
        if (prestamoExistente == null) {
            return Optional.empty();
        }

        prestamoActualizado.setId(id);
        prestamos.put(id, prestamoActualizado);
        return Optional.of(enriquecerPrestamo(prestamoActualizado));
    }

    public boolean eliminar(Long id) {
        if (id == null) return false;
        Prestamo eliminado = prestamos.remove(id);
        if (eliminado != null && "ACTIVO".equalsIgnoreCase(eliminado.getEstado())) {
            ejemplarService.cambiarEstado(eliminado.getIdEjemplar(), "DISPONIBLE");
            return true;
        }
        return eliminado != null;
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

    private Prestamo enriquecerPrestamo(Prestamo p) {
        if (p == null) return null;
        if (p.getIdEjemplar() != null) {
            ejemplarService.buscarPorId(p.getIdEjemplar()).ifPresent(p::setEjemplar);
        }
        if (p.getIdUsuario() != null) {
            usuarioService.buscarPorId(p.getIdUsuario()).ifPresent(p::setUsuario);
        }
        return p;
    }

    private PrestamoDetalladoDTO convertirADetalleDTO(Prestamo p) {
        p = enriquecerPrestamo(p);

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

        Libro libro = null;
        if (p.getEjemplar() != null) {
            libro = p.getEjemplar().getLibro();
            if (libro == null && p.getEjemplar().getIsbn() != null) {
                libro = libroService.buscarPorIsbn(p.getEjemplar().getIsbn()).orElse(null);
            }
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
                .usuario(p.getUsuario())
                .ejemplar(p.getEjemplar())
                .libro(libro)
                .multa(multa)
                .build();
    }
}

package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReporteService {

    private final LibroService libroService;
    private final EjemplarService ejemplarService;
    private final UsuarioService usuarioService;
    private final PrestamoService prestamoService;
    private final AutorService autorService;
    private final CategoriaService categoriaService;
    private final EditorialService editorialService;
    private final MultaService multaService;
    private final PagoMultaService pagoMultaService;

    public ReporteService(LibroService libroService,
                          EjemplarService ejemplarService,
                          UsuarioService usuarioService,
                          PrestamoService prestamoService,
                          AutorService autorService,
                          CategoriaService categoriaService,
                          EditorialService editorialService,
                          MultaService multaService,
                          PagoMultaService pagoMultaService) {
        this.libroService = libroService;
        this.ejemplarService = ejemplarService;
        this.usuarioService = usuarioService;
        this.prestamoService = prestamoService;
        this.autorService = autorService;
        this.categoriaService = categoriaService;
        this.editorialService = editorialService;
        this.multaService = multaService;
        this.pagoMultaService = pagoMultaService;
    }

    public Map<String, Object> obtenerDashboard() {
        List<Libro> libros = libroService.listarTodos();
        List<Ejemplar> ejemplares = ejemplarService.listarTodos();
        List<Usuario> usuarios = usuarioService.listarTodos();
        List<Prestamo> prestamos = prestamoService.listarTodos();
        List<Multa> multas = multaService.listarTodas();

        long ejemplaresDisponibles = ejemplares.stream().filter(Ejemplar::isDisponible).count();
        long ejemplaresPrestados = ejemplares.stream().filter(e -> "PRESTADO".equalsIgnoreCase(e.getEstado())).count();
        long ejemplaresMantenimiento = ejemplares.stream().filter(e -> "EN_MANTENIMIENTO".equalsIgnoreCase(e.getEstado())).count();

        long prestamosActivos = prestamos.stream().filter(p -> "ACTIVO".equalsIgnoreCase(p.getEstado())).count();
        long prestamosDevueltos = prestamos.stream().filter(p -> "DEVUELTO".equalsIgnoreCase(p.getEstado())).count();
        long prestamosVencidos = prestamos.stream().filter(p -> "VENCIDO".equalsIgnoreCase(p.getEstado())).count();

        long multasPagadas = multas.stream().filter(Multa::isPagada).count();
        long multasPendientes = multas.size() - multasPagadas;

        double montoTotalMultas = multas.stream().mapToDouble(m -> m.getMonto() != null ? m.getMonto() : 0.0).sum();
        double montoRecaudado = multas.stream().filter(Multa::isPagada).mapToDouble(m -> m.getMonto() != null ? m.getMonto() : 0.0).sum();
        double montoPendiente = montoTotalMultas - montoRecaudado;

        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("fechaReporte", LocalDate.now().toString());

        Map<String, Object> catalogo = new LinkedHashMap<>();
        catalogo.put("totalTitulosIsbn", libros.size());
        catalogo.put("totalEjemplaresFisicos", ejemplares.size());
        catalogo.put("ejemplaresDisponibles", ejemplaresDisponibles);
        catalogo.put("ejemplaresPrestados", ejemplaresPrestados);
        catalogo.put("ejemplaresEnMantenimiento", ejemplaresMantenimiento);
        catalogo.put("totalAutores", autorService.listarTodos().size());
        catalogo.put("totalCategorias", categoriaService.listarTodas().size());
        catalogo.put("totalEditoriales", editorialService.listarTodas().size());
        dashboard.put("catalogo", catalogo);

        Map<String, Object> usuariosInfo = new LinkedHashMap<>();
        usuariosInfo.put("totalRegistrados", usuarios.size());
        usuariosInfo.put("activos", usuarios.stream().filter(Usuario::isActivo).count());
        usuariosInfo.put("sancionados", usuarios.stream().filter(u -> "SANCIONADO".equalsIgnoreCase(u.getEstado())).count());
        dashboard.put("usuarios", usuariosInfo);

        Map<String, Object> prestamosInfo = new LinkedHashMap<>();
        prestamosInfo.put("totalRegistrados", prestamos.size());
        prestamosInfo.put("activos", prestamosActivos);
        prestamosInfo.put("devueltos", prestamosDevueltos);
        prestamosInfo.put("vencidos", prestamosVencidos);
        dashboard.put("prestamos", prestamosInfo);

        Map<String, Object> multasInfo = new LinkedHashMap<>();
        multasInfo.put("totalMultas", multas.size());
        multasInfo.put("pagadas", multasPagadas);
        multasInfo.put("pendientes", multasPendientes);
        multasInfo.put("montoTotal", Math.round(montoTotalMultas * 100.0) / 100.0);
        multasInfo.put("montoRecaudado", Math.round(montoRecaudado * 100.0) / 100.0);
        multasInfo.put("montoPendiente", Math.round(montoPendiente * 100.0) / 100.0);
        multasInfo.put("totalPagosProcesados", pagoMultaService.listarTodos().size());
        dashboard.put("multas", multasInfo);

        return dashboard;
    }

    public Map<String, Object> obtenerReporteLibrosPorCategoria() {
        List<Libro> libros = libroService.listarTodos();
        List<Categoria> categorias = categoriaService.listarTodas();

        Map<String, Long> librosPorCategoria = new LinkedHashMap<>();
        for (Categoria cat : categorias) {
            long count = libros.stream()
                    .filter(l -> l.getIdCategoria() != null && l.getIdCategoria().equals(cat.getId()))
                    .count();
            librosPorCategoria.put(cat.getNombre(), count);
        }

        Map<String, Object> reporte = new LinkedHashMap<>();
        reporte.put("tipoReporte", "Libros por Categoría");
        reporte.put("datos", librosPorCategoria);
        return reporte;
    }

    public Map<String, Object> obtenerReportePrestamosPorEstado() {
        List<Prestamo> prestamos = prestamoService.listarTodos();

        Map<String, Long> porEstado = prestamos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getEstado() != null ? p.getEstado().toUpperCase() : "DESCONOCIDO",
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        Map<String, Object> reporte = new LinkedHashMap<>();
        reporte.put("tipoReporte", "Préstamos por Estado");
        reporte.put("total", prestamos.size());
        reporte.put("datos", porEstado);
        return reporte;
    }

    public Map<String, Object> obtenerReporteFinancieroMultas() {
        List<Multa> multas = multaService.listarTodas();

        double montoTotal = multas.stream().mapToDouble(m -> m.getMonto() != null ? m.getMonto() : 0.0).sum();
        double recaudado = multas.stream().filter(Multa::isPagada).mapToDouble(m -> m.getMonto() != null ? m.getMonto() : 0.0).sum();
        double pendiente = montoTotal - recaudado;

        Map<String, Object> reporte = new LinkedHashMap<>();
        reporte.put("tipoReporte", "Reporte Financiero de Multas");
        reporte.put("totalMultas", multas.size());
        reporte.put("montoTotal", Math.round(montoTotal * 100.0) / 100.0);
        reporte.put("recaudado", Math.round(recaudado * 100.0) / 100.0);
        reporte.put("pendiente", Math.round(pendiente * 100.0) / 100.0);
        return reporte;
    }
}

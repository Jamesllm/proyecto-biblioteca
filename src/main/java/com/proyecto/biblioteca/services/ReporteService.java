package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final LibroService libroService;
    private final UsuarioService usuarioService;
    private final PrestamoService prestamoService;
    private final AutorService autorService;
    private final CategoriaService categoriaService;
    private final EditorialService editorialService;
    private final MultaService multaService;

    public ReporteService(LibroService libroService,
                          UsuarioService usuarioService,
                          PrestamoService prestamoService,
                          AutorService autorService,
                          CategoriaService categoriaService,
                          EditorialService editorialService,
                          MultaService multaService) {
        this.libroService = libroService;
        this.usuarioService = usuarioService;
        this.prestamoService = prestamoService;
        this.autorService = autorService;
        this.categoriaService = categoriaService;
        this.editorialService = editorialService;
        this.multaService = multaService;
    }

    public Map<String, Object> obtenerDashboard() {
        List<Libro> libros = libroService.listarTodos();
        List<Usuario> usuarios = usuarioService.listarTodos();
        List<Prestamo> prestamos = prestamoService.listarTodos();
        List<Multa> multas = multaService.listarTodas();

        long librosDisponibles = libros.stream().filter(Libro::isDisponible).count();
        long librosPrestados = libros.size() - librosDisponibles;

        long prestamosActivos = prestamos.stream().filter(p -> "ACTIVO".equalsIgnoreCase(p.getEstado())).count();
        long prestamosDevueltos = prestamos.stream().filter(p -> "DEVUELTO".equalsIgnoreCase(p.getEstado())).count();
        long prestamosVencidos = prestamos.stream().filter(p -> "VENCIDO".equalsIgnoreCase(p.getEstado())).count();

        long multasPagadas = multas.stream().filter(Multa::isPagada).count();
        long multasPendientes = multas.size() - multasPagadas;

        double montoTotalMultas = multas.stream().mapToDouble(m -> m.getMonto() != null ? m.getMonto() : 0.0).sum();
        double montoRecaudado = multas.stream().filter(Multa::isPagada).mapToDouble(m -> m.getMonto() != null ? m.getMonto() : 0.0).sum();
        double montoPendiente = montoTotalMultas - montoRecaudado;

        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("fechaReporte", java.time.LocalDate.now().toString());

        Map<String, Object> catalogo = new LinkedHashMap<>();
        catalogo.put("totalLibros", libros.size());
        catalogo.put("disponibles", librosDisponibles);
        catalogo.put("prestados", librosPrestados);
        catalogo.put("totalAutores", autorService.listarTodos().size());
        catalogo.put("totalCategorias", categoriaService.listarTodas().size());
        catalogo.put("totalEditoriales", editorialService.listarTodas().size());
        dashboard.put("catalogo", catalogo);

        Map<String, Object> usuariosInfo = new LinkedHashMap<>();
        usuariosInfo.put("totalRegistrados", usuarios.size());
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
        dashboard.put("multas", multasInfo);

        return dashboard;
    }

    public Map<String, Object> obtenerReporteLibrosPorCategoria() {
        List<Libro> libros = libroService.listarTodos();
        List<Categoria> categorias = categoriaService.listarTodas();

        Map<Long, String> mapaCategorias = categorias.stream()
                .collect(Collectors.toMap(Categoria::getId, Categoria::getNombre));

        Map<String, Long> librosPorCategoria = new LinkedHashMap<>();
        for (Categoria cat : categorias) {
            long count = libros.stream()
                    .filter(l -> cat.getId().equals(l.getIdCategoria()))
                    .count();
            librosPorCategoria.put(cat.getNombre(), count);
        }

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("totalCategorias", categorias.size());
        respuesta.put("totalLibros", libros.size());
        respuesta.put("distribucion", librosPorCategoria);
        return respuesta;
    }

    public Map<String, Object> obtenerReportePrestamos() {
        List<Prestamo> prestamos = prestamoService.listarTodos();

        List<Map<String, Object>> detallePrestamos = prestamos.stream().map(p -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("idPrestamo", p.getId());
            item.put("estado", p.getEstado());
            item.put("fechaPrestamo", p.getFechaPrestamo());
            item.put("fechaDevolucion", p.getFechaDevolucion());

            libroService.buscarPorId(p.getIdLibro()).ifPresent(libro -> {
                item.put("libroId", libro.getId());
                item.put("libroTitulo", libro.getTitulo());
            });

            usuarioService.buscarPorId(p.getIdUsuario()).ifPresent(usuario -> {
                item.put("usuarioId", usuario.getId());
                item.put("usuarioNombre", usuario.getNombre());
            });

            return item;
        }).collect(Collectors.toList());

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("totalPrestamos", prestamos.size());
        respuesta.put("detalle", detallePrestamos);
        return respuesta;
    }
}

package com.proyecto.biblioteca.dto;

import com.proyecto.biblioteca.models.Ejemplar;
import com.proyecto.biblioteca.models.Libro;
import com.proyecto.biblioteca.models.Multa;
import com.proyecto.biblioteca.models.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrestamoDetalladoDTO {
    private Long idPrestamo;
    private String estadoPrestamo; // ACTIVO, DEVUELTO, VENCIDO
    private LocalDateTime fechaHoraPrestamo;
    private LocalDateTime fechaHoraDevolucionEsperada;
    private LocalDateTime fechaHoraDevolucionReal;
    
    private Boolean enMora;
    private Long horasRetraso;
    private Integer diasRetraso;
    private Long horasRestantes;

    private Usuario usuario;
    private Ejemplar ejemplar;
    private Libro libro;
    private Multa multa;
}

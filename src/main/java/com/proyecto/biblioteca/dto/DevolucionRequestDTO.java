package com.proyecto.biblioteca.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DevolucionRequestDTO {
    // Si no se envía fechaHoraDevolucionReal, se toma LocalDateTime.now()
    private LocalDateTime fechaHoraDevolucionReal;
    
    // Estado del libro retornado: "BUENO", "DETERIORADO", "DAÑADO", "EXTRAVIADO"
    private String estadoConservacion;

    // Observaciones o motivo de daño si aplica
    private String observaciones;

    // Monto extra por daño si se desea aplicar manualmente
    private Double montoDanoExtra;
}

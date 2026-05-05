package com.innovatech.ms_tickets.dto.response;

import com.innovatech.ms_tickets.model.enums.Estado;
import com.innovatech.ms_tickets.model.enums.Prioridad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponseDTO {

    private Long id;
    private Long usuarioId;
    private String asunto;
    private String descripcion;
    private Estado estado;
    private Prioridad prioridad;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaCierre;
}

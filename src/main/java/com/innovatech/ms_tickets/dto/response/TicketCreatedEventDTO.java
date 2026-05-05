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
public class TicketCreatedEventDTO {

    private Long ticketId;
    private Long usuarioId;
    private String asunto;
    private Estado estado;
    private Prioridad prioridad;
    private LocalDateTime fechaCreacion;
    private String eventType;

    public static TicketCreatedEventDTO fromTicketResponse(TicketResponseDTO ticket) {
        return TicketCreatedEventDTO.builder()
                .ticketId(ticket.getId())
                .usuarioId(ticket.getUsuarioId())
                .asunto(ticket.getAsunto())
                .estado(ticket.getEstado())
                .prioridad(ticket.getPrioridad())
                .fechaCreacion(ticket.getFechaCreacion())
                .eventType("Ticket_Creado")
                .build();
    }
}

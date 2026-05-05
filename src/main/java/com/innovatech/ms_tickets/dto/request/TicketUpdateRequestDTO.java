package com.innovatech.ms_tickets.dto.request;

import com.innovatech.ms_tickets.model.enums.Estado;
import com.innovatech.ms_tickets.model.enums.Prioridad;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketUpdateRequestDTO {

    @NotNull(message = "El estado es obligatorio")
    private Estado estado;

    @NotNull(message = "La prioridad es obligatoria")
    private Prioridad prioridad;
}

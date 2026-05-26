package com.innovatech.ms_tickets.controller;

import com.innovatech.ms_tickets.dto.request.TicketRequestDTO;
import com.innovatech.ms_tickets.dto.request.TicketUpdateRequestDTO;
import com.innovatech.ms_tickets.dto.response.TicketResponseDTO;
import com.innovatech.ms_tickets.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "API para la gestion de tickets de soporte")
@Validated
public class TicketController {

    private final TicketService ticketService;

    @Operation(
            summary = "Crear un nuevo ticket",
            description = "Registra un nuevo ticket de soporte en el sistema y publica el evento 'Ticket_Creado' via RabbitMQ"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Ticket creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada invalidos",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping
    public ResponseEntity<TicketResponseDTO> crearTicket(
            @Valid @RequestBody TicketRequestDTO requestDTO) {
        TicketResponseDTO response = ticketService.crearTicket(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Obtener ticket por ID",
            description = "Recupera los detalles de un ticket especifico por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ticket encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ticket no encontrado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> obtenerTicketPorId(
            @Parameter(description = "ID del ticket", required = true)
            @PathVariable @Positive(message = "El id debe ser mayor a cero") Long id) {
        TicketResponseDTO response = ticketService.obtenerTicketPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar historial de tickets por usuario",
            description = "Obtiene todos los tickets asociados a un usuario especifico"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de tickets del usuario",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDTO.class))
            )
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TicketResponseDTO>> listarHistorialPorUsuario(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable @Positive(message = "El usuarioId debe ser mayor a cero") Long usuarioId) {
        List<TicketResponseDTO> response = ticketService.listarHistorialPorUsuario(usuarioId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar todos los tickets",
            description = "Obtiene el listado completo de tickets del sistema (uso administrativo)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de todos los tickets",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDTO.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> listarTodosLosTickets() {
        List<TicketResponseDTO> response = ticketService.listarTodosLosTickets();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Actualizar estado y prioridad de un ticket",
            description = "Modifica el estado y la prioridad de un ticket existente. Si el estado es CERRADO o RESUELTO, se establece la fecha de cierre automaticamente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ticket actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada invalidos",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ticket no encontrado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> actualizarTicket(
            @Parameter(description = "ID del ticket", required = true)
            @PathVariable @Positive(message = "El id debe ser mayor a cero") Long id,
            @Valid @RequestBody TicketUpdateRequestDTO requestDTO) {
        TicketResponseDTO response = ticketService.actualizarEstadoYPrioridad(id, requestDTO);
        return ResponseEntity.ok(response);
    }
}

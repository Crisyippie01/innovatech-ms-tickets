package com.innovatech.ms_tickets.service;

import com.innovatech.ms_tickets.dto.response.TicketCreatedEventDTO;
import com.innovatech.ms_tickets.dto.response.TicketResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TicketEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public void publicarTicketCreado(TicketResponseDTO ticketDTO) {
        try {
            TicketCreatedEventDTO eventDTO = TicketCreatedEventDTO.fromTicketResponse(ticketDTO);

            log.info("Publicando evento 'Ticket_Creado' para ticket ID: {}", eventDTO.getTicketId());
            rabbitTemplate.convertAndSend(exchangeName, routingKey, eventDTO);
            log.info("Evento 'Ticket_Creado' publicado exitosamente en exchange: {}", exchangeName);

        } catch (Exception e) {
            log.error("Error al publicar evento 'Ticket_Creado': {}", e.getMessage(), e);
        }
    }
}

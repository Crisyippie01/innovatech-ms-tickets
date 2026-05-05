package com.innovatech.ms_tickets.exception;

public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(String message) {
        super(message);
    }

    public TicketNotFoundException(Long ticketId) {
        super("Ticket no encontrado con ID: " + ticketId);
    }
}

package com.innovatech.ms_tickets.repository;

import com.innovatech.ms_tickets.model.Ticket;
import com.innovatech.ms_tickets.model.enums.Estado;
import com.innovatech.ms_tickets.model.enums.Prioridad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByUsuarioId(Long usuarioId);

    List<Ticket> findByEstado(Estado estado);

    List<Ticket> findByPrioridad(Prioridad prioridad);

    List<Ticket> findByUsuarioIdAndEstado(Long usuarioId, Estado estado);
}

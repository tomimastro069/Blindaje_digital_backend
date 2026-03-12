package com.blindaje.core.event.service;

import com.blindaje.core.event.domain.Evento;
import com.blindaje.core.event.repository.EventoRepository;
import org.springframework.stereotype.Service;

@Service
public class EventoService {

    private final EventoRepository eventRepository;

    public EventoService(EventoRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // TODO: Implement event CRUD and query methods
}

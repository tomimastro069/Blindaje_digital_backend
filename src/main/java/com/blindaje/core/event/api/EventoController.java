package com.blindaje.core.event.api;

import com.blindaje.core.event.service.EventoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventoController {

    private final EventoService eventService;

    public EventoController(EventoService eventService) {
        this.eventService = eventService;
    }

    // TODO: Implement event endpoints
}

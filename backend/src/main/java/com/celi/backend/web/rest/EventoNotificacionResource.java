package com.celi.backend.web.rest;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.celi.backend.security.AuthoritiesConstants;
import com.celi.backend.service.catedra.EventoNotificacionService;
import com.celi.backend.service.dto.NotificacionEventoDTO;

@RestController
@RequestMapping("/api/admin/eventos")
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
public class EventoNotificacionResource {

    private static final Logger LOG = LoggerFactory.getLogger(EventoNotificacionResource.class);

    private final EventoNotificacionService eventoNotificacionService;

    public EventoNotificacionResource(EventoNotificacionService eventoNotificacionService) {
        this.eventoNotificacionService = eventoNotificacionService;
    }

    @PostMapping("/notificacion")
    public ResponseEntity<Void> recibirNotificacion(@Valid @RequestBody NotificacionEventoDTO notificacion) {
        LOG.info("REST request para recibir notificación de cambio de evento: eventoIdCatedra={}, tipoCambio={}",
                notificacion.getEventoIdCatedra(), notificacion.getTipoCambio());

        eventoNotificacionService.procesarNotificacion(notificacion);
        return ResponseEntity.noContent().build();
    }
}

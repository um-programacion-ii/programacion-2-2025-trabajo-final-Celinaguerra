package com.celi.backend.web.rest;

import com.celi.backend.service.CatedraSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catedra")
public class CatedraResource {

    private final CatedraSyncService catedraSyncService;

    public CatedraResource(CatedraSyncService catedraSyncService) {
        this.catedraSyncService = catedraSyncService;
    }

    @PostMapping("/sync")
    public ResponseEntity<Void> syncEventos() {
        catedraSyncService.syncEventos();
        return ResponseEntity.ok().build();
    }
}

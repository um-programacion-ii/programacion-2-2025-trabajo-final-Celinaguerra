package com.celi.proxy.web.rest;

import com.celi.proxy.config.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * Proxy que redirige peticiones a la API de Cátedra.
 * Intermediario entre backend y Cátedra.
 */
@RestController
@RequestMapping("/api/catedra")
@Slf4j
public class CatedraProxyResource {

    private final RestTemplate catedraRestTemplate;
    private final ApplicationProperties applicationProperties;

    public CatedraProxyResource(RestTemplate catedraRestTemplate, ApplicationProperties applicationProperties) {
        this.catedraRestTemplate = catedraRestTemplate;
        this.applicationProperties = applicationProperties;
    }

    /**
     * GET /api/catedra/eventos-resumidos
     * Redirige a Cátedra: /api/endpoints/v1/eventos-resumidos (Payload 3)
     */
    @GetMapping("/eventos-resumidos")
    public ResponseEntity<String> getEventosResumidos() {
        String url = buildCatedraUrl("/endpoints/v1/eventos-resumidos");
        log.debug("Redirigiendo GET request a Cátedra: {}", url);
        return forwardGet(url);
    }

    /**
     * GET /api/catedra/eventos
     * Redirige a Cátedra: /api/endpoints/v1/eventos (Payload 4)
     */
    @GetMapping("/eventos")
    public ResponseEntity<String> getEventos() {
        String url = buildCatedraUrl("/endpoints/v1/eventos");
        log.debug("Redirigiendo GET request a Cátedra: {}", url);
        return forwardGet(url);
    }

    /**
     * GET /api/catedra/evento/{id}
     * Redirige a Cátedra: /api/endpoints/v1/evento/{id} (Payload 5)
     */
    @GetMapping("/evento/{id}")
    public ResponseEntity<String> getEvento(@PathVariable Long id) {
        String url = buildCatedraUrl("/endpoints/v1/evento/" + id);
        log.debug("Redirigiendo GET request a Cátedra: {}", url);
        return forwardGet(url);
    }

    /**
     * POST /api/catedra/bloquear-asientos
     * Redirige a Cátedra: /api/endpoints/v1/bloquear-asientos (Payload 6)
     */
    @PostMapping("/bloquear-asientos")
    public ResponseEntity<String> bloquearAsientos(@RequestBody String body) {
        String url = buildCatedraUrl("/endpoints/v1/bloquear-asientos");
        log.debug("Redirigiendo POST request a Cátedra: {}", url);
        return forwardPost(url, body);
    }

    /**
     * POST /api/catedra/realizar-venta
     * Redirige a Cátedra: /api/endpoints/v1/realizar-venta (Payload 7)
     */
    @PostMapping("/realizar-venta")
    public ResponseEntity<String> realizarVenta(@RequestBody String body) {
        String url = buildCatedraUrl("/endpoints/v1/realizar-venta");
        log.debug("Redirigiendo POST request a Cátedra: {}", url);
        return forwardPost(url, body);
    }

    /**
     * GET /api/catedra/listar-ventas
     * Redirige a Cátedra: /api/endpoints/v1/listar-ventas (Payload 8)
     */
    @GetMapping("/listar-ventas")
    public ResponseEntity<String> listarVentas() {
        String url = buildCatedraUrl("/endpoints/v1/listar-ventas");
        log.debug("Redirigiendo GET request a Cátedra: {}", url);
        return forwardGet(url);
    }

    /**
     * GET /api/catedra/listar-venta/{id}
     * Redirige a Cátedra: /api/endpoints/v1/listar-venta/{id} (Payload 9)
     */
    @GetMapping("/listar-venta/{id}")
    public ResponseEntity<String> listarVenta(@PathVariable Long id) {
        String url = buildCatedraUrl("/endpoints/v1/listar-venta/" + id);
        log.debug("Redirigiendo GET request a Cátedra: {}", url);
        return forwardGet(url);
    }

    // Métodos auxiliares

    private String buildCatedraUrl(String path) {
        return applicationProperties.getCatedra().getUrl() + path;
    }

    private ResponseEntity<String> forwardGet(String url) {
        try {
            return catedraRestTemplate.getForEntity(url, String.class);
        } catch (Exception e) {
            log.error("Error redirigiendo GET request a Cátedra: {}", e.getMessage());
            throw e;
        }
    }

    private ResponseEntity<String> forwardPost(String url, String body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            return catedraRestTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            log.error("Error redirigiendo POST request a Cátedra: {}", e.getMessage());
            throw e;
        }
    }
}

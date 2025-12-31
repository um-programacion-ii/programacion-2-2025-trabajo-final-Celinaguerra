package com.celi.backend.service.warmup;

import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.celi.backend.repository.EventoRepository;
import com.celi.backend.service.dto.BloqueoAsientosRequestDTO;
import com.celi.backend.service.dto.BloqueoAsientosResponseDTO;
import com.celi.backend.service.dto.MapaAsientosDTO;
import com.celi.backend.service.proxy.ProxyAsientosService;

/**
 * Servicio para hacer warm-up de Redis al iniciar la aplicación.
 * Bloquea temporalmente 1 asiento de cada evento activo para asegurar
 * que Redis tenga datos actualizados.
 */
@Service
public class RedisWarmupService {

    private static final Logger LOG = LoggerFactory.getLogger(RedisWarmupService.class);

    private final EventoRepository eventoRepository;
    private final ProxyAsientosService proxyAsientosService;

    public RedisWarmupService(
            EventoRepository eventoRepository,
            ProxyAsientosService proxyAsientosService) {
        this.eventoRepository = eventoRepository;
        this.proxyAsientosService = proxyAsientosService;
    }

    /**
     * Ejecuta el warm-up de Redis de forma asíncrona.
     * Intenta bloquear 1 asiento de cada evento activo hasta lograrlo.
     */
    @Async
    public void warmupRedis() {
        LOG.info("Iniciando warm-up de Redis...");

        try {
            // Esperar un poco para asegurar que el proxy esté listo
            // Esto es importante porque el warm-up se ejecuta al iniciar la aplicación
            try {
                Thread.sleep(5000); // Esperar 5 segundos
                LOG.debug("Espera inicial completada, el proxy debería estar listo");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.warn("Interrupción durante la espera inicial del warm-up");
                return;
            }

            // Obtener todos los eventos activos
            List<com.celi.backend.domain.Evento> eventos = eventoRepository.findEventosActivos(Instant.now());
            LOG.info("Encontrados {} eventos activos para warm-up", eventos.size());

            int exitosos = 0;
            int fallidos = 0;

            for (com.celi.backend.domain.Evento evento : eventos) {
                Long eventoId = evento.getEventoIdCatedra();
                if (eventoId == null) {
                    LOG.warn("Evento {} no tiene eventoIdCatedra, saltando", evento.getId());
                    fallidos++;
                    continue;
                }

                if (intentarBloquearAsiento(eventoId)) {
                    exitosos++;
                    LOG.debug("Warm-up exitoso para eventoId: {}", eventoId);
                } else {
                    fallidos++;
                    LOG.warn("No se pudo hacer warm-up para eventoId: {}", eventoId);
                }
            }

            LOG.info("Warm-up de Redis completado. Exitosos: {}, Fallidos: {}", exitosos, fallidos);
        } catch (Exception e) {
            LOG.error("Error durante el warm-up de Redis", e);
        }
    }

    /**
     * Intenta bloquear un asiento del evento hasta lograrlo o quedarse sin
     * asientos.
     * Si no puede bloquear ningún asiento con el método normal, recorre toda la
     * matriz.
     * 
     * @param eventoId ID del evento en la cátedra
     * @return true si logró bloquear un asiento, false en caso contrario
     */
    private boolean intentarBloquearAsiento(Long eventoId) {
        try {
            // Obtener mapa de asientos
            MapaAsientosDTO mapa = proxyAsientosService.obtenerMapaAsientos(eventoId);

            if (mapa == null) {
                LOG.warn("Mapa de asientos es null para eventoId: {}", eventoId);
                // Intentar bloquear asiento por defecto (fila 1, columna 1) para inicializar
                // Redis
                if (bloquearAsiento(eventoId, "1", 1)) {
                    return true;
                }
                // Si falla, intentar recorrer toda la matriz
                return recorrerTodaMatriz(eventoId);
            }

            List<MapaAsientosDTO.AsientoDTO> asientos = mapa.getAsientos();

            // Si el mapa está vacío, Redis probablemente no tiene datos aún
            // Intentar bloquear un asiento por defecto para inicializar Redis
            if (asientos == null || asientos.isEmpty()) {
                LOG.info(
                        "Mapa de asientos vacío para eventoId: {} (Redis sin inicializar), intentando bloquear asiento por defecto",
                        eventoId);
                // Intentar con varios asientos comunes: fila 1 columna 1, fila 1 columna 2,
                // etc.
                // También intentar con diferentes filas por si acaso
                for (int fila = 1; fila <= 3; fila++) {
                    for (int columna = 1; columna <= 5; columna++) {
                        if (bloquearAsiento(eventoId, String.valueOf(fila), columna)) {
                            LOG.info(
                                    "✅ Warm-up exitoso para eventoId: {} usando asiento por defecto (fila {}, columna {})",
                                    eventoId, fila, columna);
                            return true;
                        }
                    }
                }
                LOG.warn(
                        "⚠️ No se pudo bloquear ningún asiento por defecto para eventoId: {}, recorriendo toda la matriz",
                        eventoId);
                // Si no pudo bloquear ningún asiento por defecto, recorrer toda la matriz
                return recorrerTodaMatriz(eventoId);
            }

            // Buscar el primer asiento LIBRE en el mapa
            int intentos = 0;
            int maxIntentos = Math.min(asientos.size(), 20); // Intentar máximo 20 asientos

            for (MapaAsientosDTO.AsientoDTO asiento : asientos) {
                if (intentos >= maxIntentos) {
                    break;
                }

                if (asiento.getEstado() == MapaAsientosDTO.AsientoDTO.EstadoAsiento.LIBRE &&
                        asiento.getFila() != null &&
                        asiento.getNumero() != null) {

                    intentos++;
                    // Intentar bloquear este asiento
                    if (bloquearAsiento(eventoId, asiento.getFila(), asiento.getNumero())) {
                        LOG.debug("Warm-up exitoso para eventoId: {} usando asiento (fila {}, columna {})",
                                eventoId, asiento.getFila(), asiento.getNumero());
                        return true;
                    }
                    // Si falla, continuar con el siguiente asiento
                }
            }

            LOG.warn(
                    "No se encontró ningún asiento libre para bloquear en eventoId: {} después de {} intentos, recorriendo toda la matriz",
                    eventoId, intentos);
            // Si no pudo bloquear ningún asiento libre, recorrer toda la matriz
            return recorrerTodaMatriz(eventoId);
        } catch (Exception e) {
            LOG.error("Error al intentar bloquear asiento para eventoId: {}", eventoId, e);
            // En caso de error, intentar recorrer toda la matriz como último recurso
            return recorrerTodaMatriz(eventoId);
        }
    }

    /**
     * Recorre toda la matriz de asientos del evento intentando bloquear cada uno.
     * 
     * @param eventoId ID del evento en la cátedra
     * @return true si logró bloquear un asiento, false en caso contrario
     */
    private boolean recorrerTodaMatriz(Long eventoId) {
        try {
            LOG.info("🔄 Recorriendo toda la matriz para eventoId: {}", eventoId);

            // Obtener el evento para acceder a las dimensiones
            java.util.Optional<com.celi.backend.domain.Evento> eventoOpt = eventoRepository
                    .findByEventoIdCatedra(eventoId);

            if (eventoOpt.isEmpty()) {
                LOG.warn("No se encontró el evento con eventoIdCatedra: {}", eventoId);
                return false;
            }

            com.celi.backend.domain.Evento evento = eventoOpt.get();
            Integer filas = evento.getFilaAsiento();
            Integer columnas = evento.getColumnAsiento();

            if (filas == null || columnas == null || filas <= 0 || columnas <= 0) {
                LOG.warn("Evento {} no tiene dimensiones válidas (filas={}, columnas={})", eventoId, filas, columnas);
                return false;
            }

            LOG.info("Recorriendo matriz completa: {} filas x {} columnas para eventoId: {}", filas, columnas,
                    eventoId);

            // Recorrer toda la matriz fila por fila, columna por columna
            for (int fila = 1; fila <= filas; fila++) {
                for (int columna = 1; columna <= columnas; columna++) {
                    if (bloquearAsiento(eventoId, String.valueOf(fila), columna)) {
                        LOG.info("✅ Warm-up exitoso recorriendo matriz: eventoId={}, fila={}, columna={}", eventoId,
                                fila, columna);
                        return true;
                    }
                    // Pequeña pausa para no saturar el proxy
                    try {
                        Thread.sleep(50); // 50ms entre intentos
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        LOG.warn("Interrupción durante el recorrido de matriz");
                        return false;
                    }
                }
            }

            LOG.warn(
                    "❌ No se pudo bloquear ningún asiento después de recorrer toda la matriz ({} filas x {} columnas) para eventoId: {}",
                    filas, columnas, eventoId);
            return false;
        } catch (Exception e) {
            LOG.error("Error al recorrer toda la matriz para eventoId: {}", eventoId, e);
            return false;
        }
    }

    /**
     * Bloquea un asiento específico.
     * 
     * @param eventoId ID del evento
     * @param fila     Fila del asiento (String, puede ser número o letra)
     * @param numero   Número del asiento
     * @return true si el bloqueo fue exitoso, false en caso contrario
     */
    private boolean bloquearAsiento(Long eventoId, String fila, Integer numero) {
        try {
            // Crear request de bloqueo
            BloqueoAsientosRequestDTO request = new BloqueoAsientosRequestDTO();
            request.setEventoId(eventoId);

            BloqueoAsientosRequestDTO.AsientoBloqueoDTO asientoDto = new BloqueoAsientosRequestDTO.AsientoBloqueoDTO();
            asientoDto.setFila(convertirFilaAInteger(fila)); // Convertir String a Integer
            asientoDto.setColumna(numero); // El método correcto es setColumna

            request.setAsientos(List.of(asientoDto));

            // Intentar bloquear
            BloqueoAsientosResponseDTO respuesta = proxyAsientosService.bloquearAsientos(request);

            if (respuesta != null && Boolean.TRUE.equals(respuesta.getExitoso())) {
                LOG.info("✅ Asiento bloqueado exitosamente para warm-up: eventoId={}, fila={}, numero={}", eventoId,
                        fila, numero);
                return true;
            } else {
                String mensaje = respuesta != null ? respuesta.getMensaje() : "Respuesta nula";

                // Verificar si es un error de conexión (proxy no disponible)
                if (mensaje != null && (mensaje.contains("I/O error") || mensaje.contains("Connection refused") ||
                        mensaje.contains("connect timed out") || mensaje.contains("No route to host"))) {
                    LOG.warn("⚠️ Proxy no disponible para warm-up: eventoId={}, fila={}, numero={}, error={}",
                            eventoId, fila, numero, mensaje);
                    // No es un error crítico, el warm-up puede fallar si el proxy no está listo
                    return false;
                }

                LOG.debug("❌ No se pudo bloquear asiento: eventoId={}, fila={}, numero={}, motivo={}",
                        eventoId, fila, numero, mensaje);
                // Si la respuesta indica que el asiento no está disponible, es normal (puede
                // estar ocupado)
                if (mensaje != null && (mensaje.contains("no disponible") || mensaje.contains("ocupado")
                        || mensaje.contains("bloqueado"))) {
                    LOG.debug("Asiento no disponible (esperado): eventoId={}, fila={}, numero={}", eventoId, fila,
                            numero);
                }
                return false;
            }
        } catch (Exception e) {
            LOG.warn("Excepción al bloquear asiento para warm-up: eventoId={}, fila={}, numero={}, error={}",
                    eventoId, fila, numero, e.getMessage());
            return false;
        }
    }

    /**
     * Convierte una fila de String a Integer.
     * Si es una letra (A-Z), la convierte a número (A=1, B=2, etc.).
     * Si es un número, lo parsea directamente.
     */
    private Integer convertirFilaAInteger(String fila) {
        if (fila == null || fila.isEmpty()) {
            return null;
        }

        String filaUpper = fila.trim().toUpperCase();

        // Si es una letra (A-Z)
        if (filaUpper.length() == 1 && filaUpper.charAt(0) >= 'A' && filaUpper.charAt(0) <= 'Z') {
            return filaUpper.charAt(0) - 'A' + 1; // A=1, B=2, C=3, etc.
        }

        // Si es un número, intentar parsearlo
        try {
            return Integer.parseInt(filaUpper);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

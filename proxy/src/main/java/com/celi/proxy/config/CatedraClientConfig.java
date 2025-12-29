package com.celi.proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Configuración para el cliente de la API de Cátedra.
 */
@Configuration
public class CatedraClientConfig {

    private final ApplicationProperties applicationProperties;

    public CatedraClientConfig(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public RestTemplate catedraRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Inyecta el token en todas las peticiones
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            String token = applicationProperties.getCatedra().getToken();
            if (token != null && !token.isEmpty()) {
                request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }
            return execution.execute(request, body);
        };

        restTemplate.setInterceptors(Collections.singletonList(interceptor));
        return restTemplate;
    }
}

package com.celi.proxy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application")
@Data
public class ApplicationProperties {

    private final Security security = new Security();
    private final Backend backend = new Backend();
    private final Catedra catedra = new Catedra();

    @Data
    public static class Security {
        private final Jwt jwt = new Jwt();

        @Data
        public static class Jwt {
            private String base64Secret;
        }
    }

    @Data
    public static class Backend {
        private String url;
    }

    @Data
    public static class Catedra {
        private String url;
        private String token;
    }
}

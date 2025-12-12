package com.celi.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TipoEventoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TipoEvento getTipoEventoSample1() {
        return new TipoEvento().id(1L).nombre("nombre1").descripcion("descripcion1");
    }

    public static TipoEvento getTipoEventoSample2() {
        return new TipoEvento().id(2L).nombre("nombre2").descripcion("descripcion2");
    }

    public static TipoEvento getTipoEventoRandomSampleGenerator() {
        return new TipoEvento()
            .id(longCount.incrementAndGet())
            .nombre(UUID.randomUUID().toString())
            .descripcion(UUID.randomUUID().toString());
    }
}

package dev.jose.mastersys.factory;


import dev.jose.mastersys.plano.dto.PlanoRequest;

import java.math.BigDecimal;

public class PlanoTestFactory {

    private PlanoTestFactory() {}

    public static PlanoRequest planoRequest() {

        return new PlanoRequest(
                "mensal",
                new BigDecimal("130.00"),
                1L
        );
    }
}

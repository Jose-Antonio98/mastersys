package dev.jose.mastersys.relatorio.projection;

import java.math.BigDecimal;

public interface FaturamentoMensalProjection {

    String getMes();
    BigDecimal getTotal();
}

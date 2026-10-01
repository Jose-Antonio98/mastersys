package dev.jose.mastersys.relatorio.projection;

import dev.jose.mastersys.fatura.domain.enums.StatusFatura;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface FaturaRelatorioProjection {

    Long getMatriculaId();
    String getAlunoNome();
    LocalDate getDataVencimento();
    LocalDateTime getDataPagamento();
    LocalDate getDataCancelamento();
    BigDecimal getValor();
    StatusFatura getStatusFatura();
}

package dev.jose.mastersys.fatura.dto;

import dev.jose.mastersys.fatura.domain.FaturaMatricula;
import dev.jose.mastersys.fatura.domain.enums.StatusFatura;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FaturaMatriculaResponse(
        long id,
        LocalDate dataVencimento,
        BigDecimal valor,
        LocalDateTime dataPagamento,
        LocalDate dataCancelamento,
        StatusFatura status,
        Long matriculaId
) {

    public static FaturaMatriculaResponse fromEntity(FaturaMatricula faturaMatricula) {
        return new FaturaMatriculaResponse(
                faturaMatricula.getId(),
                faturaMatricula.getDataVencimento(),
                faturaMatricula.getValor(),
                faturaMatricula.getDataPagamento(),
                faturaMatricula.getDataCancelamento(),
                faturaMatricula.getStatusFatura(),
                faturaMatricula.getMatricula().getId()
        );
    }
}

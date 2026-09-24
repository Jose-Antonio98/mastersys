package dev.jose.mastersys.fatura.dto;

import dev.jose.mastersys.fatura.domain.enums.StatusFatura;

import java.time.LocalDate;

public record FaturaFiltroRequest (
        Long matriculaId,
        StatusFatura status,
        LocalDate dataInicio,
        LocalDate dataFim
) {
}

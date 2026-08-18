package dev.jose.mastersys.dto;

import dev.jose.mastersys.domain.enums.StatusMatricula;

import java.time.LocalDate;

public record MatriculaFiltroRequest(
        Integer diaVencimento,
        StatusMatricula status,
        Long alunoId,
        String nomeAluno,
        LocalDate dataMatriculaInicio,
        LocalDate dataMatriculaFim,
        LocalDate dataEncerramentoInicio,
        LocalDate dataEncerramentoFim
) {
}

package dev.jose.mastersys.fatura.dto;

import jakarta.validation.constraints.NotNull;

public record FaturaMatriculaRequest(
        @NotNull
        Long matriculaId) {
}

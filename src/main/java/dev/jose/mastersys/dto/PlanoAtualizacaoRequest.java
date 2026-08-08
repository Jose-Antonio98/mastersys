package dev.jose.mastersys.dto;

import dev.jose.mastersys.domain.Plano;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PlanoAtualizacaoRequest(

        @NotBlank(message = "O nome do plano é obrigatório.")
        @Size(max = 100, message = "O nome não pode ultrapassar 100 caracteres.")
        String nome,

        @NotNull(message = "O valor é obrigatorio.")
        @PositiveOrZero(message = "O valor do plano não pode ser negativo.")
        BigDecimal valor
) {

    public void preencher (Plano plano) {
        plano.setNome(nome);
        plano.setValorMensal(valor);
    }
}

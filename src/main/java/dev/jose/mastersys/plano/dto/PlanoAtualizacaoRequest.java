package dev.jose.mastersys.plano.dto;

import dev.jose.mastersys.plano.domain.Plano;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PlanoAtualizacaoRequest(

        @NotBlank(message = "O nome do plano é obrigatório.")
        @Size(max = 100, message = "O nome não pode ultrapassar 100 caracteres.")
        String nome,

        @NotNull(message = "O valor é obrigatório.")
        @Positive(message = "O valor do plano não pode ser negativo.")
        BigDecimal valor
) {

    public void preencher (Plano plano) {
        if (nome != null ) plano.setNome(nome);
        if (valor != null ) plano.setValor(valor);
    }
}

package dev.jose.mastersys.plano.dto;

import dev.jose.mastersys.plano.domain.Plano;

import java.math.BigDecimal;

public record PlanoResponse(
        Long id,
        String nome,
        Boolean ativo,
        Long modalidadeId,
        String modalidade,
        BigDecimal valor
) {

    public static PlanoResponse fromEntity(Plano plano) {
        return new PlanoResponse(
                plano.getId(),
                plano.getNome(),
                plano.getAtivo(),
                plano.getModalidade().getId(),
                plano.getModalidade().getNome(),
                plano.getValorMensal()
        );
    }
}

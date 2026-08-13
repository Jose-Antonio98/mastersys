package dev.jose.mastersys.dto;

import dev.jose.mastersys.domain.Plano;

public record PlanoResponse(
        Long id,
        String nome,
        Boolean ativo,
        Long modalidadeId,
        String modalidade
) {

    public static PlanoResponse fromEntity(Plano plano) {
        return new PlanoResponse(
                plano.getId(),
                plano.getNome(),
                plano.getAtivo(),
                plano.getModalidade().getId(),
                plano.getModalidade().getNome()
        );
    }
}

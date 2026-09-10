package dev.jose.mastersys.modalidade.dto;

import dev.jose.mastersys.modalidade.domain.Modalidade;

public record ModalidadeResponse(
        Long id,
        String nome,
        boolean ativa
) {

    public static ModalidadeResponse fromEntity(Modalidade modalidade) {
        return new ModalidadeResponse(
                modalidade.getId(),
                modalidade.getNome(),
                modalidade.getAtiva()
        );
    }
}

package dev.jose.mastersys.dto;

import dev.jose.mastersys.domain.Modalidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ModalidadeRequest(

        @NotBlank(message = "O nome da modalidade é obrigatório.")
        @Size(min = 3, max = 100, message = "O nome deve conter entre 3 e 100 caracteres.")
        String nome

) {
    public Modalidade toEntity() {
        var modalidade = new Modalidade();
        preencher(modalidade);
        return modalidade;
    }

    public void preencher(Modalidade modalidade) {
        modalidade.setNome(nome);
    }
}

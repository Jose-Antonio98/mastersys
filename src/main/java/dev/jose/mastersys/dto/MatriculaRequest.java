package dev.jose.mastersys.dto;

import dev.jose.mastersys.domain.Aluno;
import dev.jose.mastersys.domain.Matricula;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MatriculaRequest(

        @NotNull(message = "O identificador do aluno é obrigatório")
        Long alunoId,

        @NotNull(message = "O dia do vencimento é obrigatório")
        @Min(value = 1, message = "O dia de vencimento deve ser entre 1 e 31")
        @Max(value = 31, message = "O dia de vencimento deve ser entre 1 e 31")
        Integer diaVencimento
) {

    public Matricula toEntity(Aluno aluno) {
        var matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setDiaVencimento(diaVencimento);
        return matricula;
    }
}

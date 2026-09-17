package dev.jose.mastersys.matricula.dto;


import dev.jose.mastersys.matricula.domain.Matricula;
import dev.jose.mastersys.matricula.domain.MatriculaModalidade;
import dev.jose.mastersys.plano.domain.Plano;
import jakarta.validation.constraints.NotNull;

public record MatriculaModalidadeRequest(

        @NotNull(message = "O identificador do Matrícula é obrigatório")
        Long matriculaId,

        @NotNull(message = "O identificador do Plano é obrigatório")
        Long planoId
) {

    public MatriculaModalidade toEntity(Plano plano, Matricula matricula) {
        var matriculaModalidade = new MatriculaModalidade();
        matriculaModalidade.setMatricula(matricula);
        matriculaModalidade.setModalidade(plano.getModalidade());
        matriculaModalidade.setPlano(plano);
        return matriculaModalidade;
    }
}

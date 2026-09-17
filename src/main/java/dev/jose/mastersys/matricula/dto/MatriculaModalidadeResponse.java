package dev.jose.mastersys.matricula.dto;

import dev.jose.mastersys.matricula.domain.MatriculaModalidade;
import java.time.LocalDate;

public record MatriculaModalidadeResponse(
        Long id,
        LocalDate dataInicio,
        LocalDate dataFim,
        Long matriculaId,
        Long modalidadeId,
        Long planoId
) {

    public static MatriculaModalidadeResponse fromEntity(MatriculaModalidade matriculaModalidade) {
        return new MatriculaModalidadeResponse(
                matriculaModalidade.getId(),
                matriculaModalidade.getDataInicio(),
                matriculaModalidade.getDataFim(),
                matriculaModalidade.getMatricula().getId(),
                matriculaModalidade.getModalidade().getId(),
                matriculaModalidade.getPlano().getId()
        );
    }
}

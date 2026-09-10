package dev.jose.mastersys.matricula.dto;

import dev.jose.mastersys.aluno.domain.Aluno;
import dev.jose.mastersys.matricula.domain.Matricula;
import dev.jose.mastersys.matricula.domain.enums.StatusMatricula;

import java.time.LocalDate;

public record MatriculaResponse(
        Long id,
        LocalDate dataMatricula,
        Integer diaVencimento,
        LocalDate dataEncerramento,
        StatusMatricula status,
        Aluno aluno
) {

    public static MatriculaResponse fromEntity(Matricula matricula) {
        return new MatriculaResponse(
                matricula.getId(),
                matricula.getDataMatricula(),
                matricula.getDiaVencimento(),
                matricula.getDataEncerramento(),
                matricula.getStatus(),
                matricula.getAluno()
        );
    }
}

package dev.jose.mastersys.dto;

import dev.jose.mastersys.domain.Aluno;
import dev.jose.mastersys.domain.enums.Sexo;

import java.time.LocalDate;
import java.time.LocalDateTime;


public record AlunoResponse(
        Long id,
        String nome,
        LocalDate dataNascimento,
        Sexo sexo,
        String celular,
        String email,
        String cidade,
        String estado,
        String observacoes,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {

    public static AlunoResponse fromEntity(Aluno aluno) {
        return new AlunoResponse(
                aluno.getId(),
                aluno.getNome(),
                aluno.getDataNascimento(),
                aluno.getSexo(),
                aluno.getCelular(),
                aluno.getEmail(),
                aluno.getCidade(),
                aluno.getEstado(),
                aluno.getObservacao(),
                aluno.getCriadoEm(),
                aluno.getAtualizadoEm()
        );
    }
}

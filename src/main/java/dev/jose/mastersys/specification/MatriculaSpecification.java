package dev.jose.mastersys.specification;


import dev.jose.mastersys.domain.Matricula;
import dev.jose.mastersys.domain.enums.StatusMatricula;
import dev.jose.mastersys.dto.MatriculaFiltroRequest;
import org.springframework.data.jpa.domain.Specification;

import java.text.Normalizer;
import java.time.LocalDate;


public class MatriculaSpecification {

    public static Specification<Matricula> filtros(MatriculaFiltroRequest filtro) {
        return Specification.where(alunoIdIgual(filtro.alunoId()))
                .and(nomeAlunoContem(filtro.nomeAluno()))
                .and(statusIgual(filtro.status()))
                .and(diaVencimentoIgual(filtro.diaVencimento()))
                .and(dataEntre("dataMatricula", filtro.dataMatriculaInicio(), filtro.dataMatriculaFim()))
                .and(dataEntre("dataEncerramento",filtro.dataEncerramentoInicio(), filtro.dataEncerramentoFim()));
    }

    private static Specification<Matricula> alunoIdIgual(Long alunoId) {
        return (root, ignoredQuery, criteriaBuilder) -> {
            if (alunoId == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("aluno").get("id"), alunoId);
        };
    }

    private static Specification<Matricula> nomeAlunoContem(String nomeAluno) {
        return (root, ignoredQuery, criteriaBuilder) -> {
            if (nomeAluno == null || nomeAluno.isBlank()) {
                return null;
            }

            var aluno = root.join("aluno");

            return criteriaBuilder.like(criteriaBuilder.function("unaccent",
                            String.class,criteriaBuilder.lower(aluno.get("nome"))),
                    "%" + removerAcentos(nomeAluno.toLowerCase()) + "%");
        };
    }

    private static Specification<Matricula> statusIgual(StatusMatricula status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    private static Specification<Matricula> diaVencimentoIgual(Integer diaVencimento) {
        return (root, query, criteriaBuilder) -> {
            if (diaVencimento == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("diaVencimento"), diaVencimento);
        };
    }

    private static Specification<Matricula> dataEntre(String campo, LocalDate inicio, LocalDate fim) {
        return (root, query, criteriaBuilder) ->  {
            if (inicio == null && fim == null) {
                return null;
            }

            if (inicio != null && fim != null) {
                return criteriaBuilder.between(root.get(campo), inicio, fim);
            }

            if (inicio != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(campo), inicio);
            }

            return criteriaBuilder.lessThanOrEqualTo(root.get(campo), fim);
        };
    }


    private static String removerAcentos(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}

package dev.jose.mastersys.specification;

import dev.jose.mastersys.domain.Aluno;
import dev.jose.mastersys.dto.AlunoFiltroRequest;
import org.springframework.data.jpa.domain.Specification;

import java.text.Normalizer;


public class AlunoSpecification {

    public static Specification<Aluno> filtros(AlunoFiltroRequest filtro) {
        return Specification.where(nomeContem(filtro.nome()))
                .and(emailContem(filtro.email()))
                .and(celularContem(filtro.celular()))
                .and(cidadeContem(filtro.cidade()))
                .and(estadoIgual(filtro.estado()));
    }

    private static Specification<Aluno> nomeContem(String nome) {
        return (root, ignoredQuery, criteriaBuilder) -> {
            if (nome == null || nome.isBlank()) {
                return null;
            }

            return criteriaBuilder.like(criteriaBuilder.function("unaccent",
                    String.class,criteriaBuilder.lower(root.get("nome"))),
                    "%" + removerAcentos(nome.toLowerCase()) + "%");

        };
    }

    private static Specification<Aluno> emailContem(String email) {
        return (root, ignoredQuery, criteriaBuilder) -> {
            if (email == null || email.isBlank()) {
                return null;
            }

            return criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                    "%" + email.toLowerCase() + "%");
        };
    }

    private static Specification<Aluno> celularContem(String celular) {
        return (root, ignoredQuery, criteriaBuilder) -> {
            if (celular == null || celular.isBlank()) {
                return null;
            }

            return criteriaBuilder.like(criteriaBuilder.lower(root.get("celular")),
                    "%" + celular.toLowerCase() + "%");
        };
    }

    private static Specification<Aluno> cidadeContem(String cidade) {
        return (root, ignoredQuery, criteriaBuilder) -> {
            if (cidade == null || cidade.isBlank()) {
                return null;
            }

            return criteriaBuilder.like(criteriaBuilder.function("unaccent",
                            String.class,criteriaBuilder.lower(root.get("cidade"))),
                    "%" + removerAcentos(cidade.toLowerCase()) + "%");

        };
    }

    private static Specification<Aluno> estadoIgual(String estado) {
        return (root, ignoredQuery, criteriaBuilder) -> {
            if (estado == null || estado.isBlank()) {
                return null;
            }

            return criteriaBuilder.equal(criteriaBuilder.upper(root.get("estado")),
                    estado.toUpperCase());
        };
    }

    private static String removerAcentos(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}

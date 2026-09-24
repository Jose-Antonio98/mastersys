package dev.jose.mastersys.fatura.specification;

import dev.jose.mastersys.fatura.domain.FaturaMatricula;
import dev.jose.mastersys.fatura.domain.enums.StatusFatura;
import dev.jose.mastersys.fatura.dto.FaturaFiltroRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;


public class FaturaMatriculaSpecification {
    public static Specification<FaturaMatricula> filtros (FaturaFiltroRequest filtro) {
        return Specification.where(matriculaIgual(filtro.matriculaId()))
                .and(statusIgual(filtro.status()))
                .and(faturaPeriodoEntre("dataVencimento", filtro.dataInicio(), filtro.dataFim()));
    }

    private static Specification<FaturaMatricula> matriculaIgual(Long matriculaId) {
        return (root, query, criteriaBuilder) -> {
            if (matriculaId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("matricula").get("id"), matriculaId);
        };
    }

    private static Specification<FaturaMatricula> statusIgual(StatusFatura status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("statusFatura"), status);
        };
    }

    private static Specification<FaturaMatricula> faturaPeriodoEntre(String campo, LocalDate inicio, LocalDate fim) {
        return (root, query, criteriaBuilder) -> {
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


}

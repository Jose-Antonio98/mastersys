package dev.jose.mastersys.fatura.repository;

import dev.jose.mastersys.fatura.domain.FaturaMatricula;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaturaMatriculaRepository extends JpaRepository<FaturaMatricula, Long> {
}

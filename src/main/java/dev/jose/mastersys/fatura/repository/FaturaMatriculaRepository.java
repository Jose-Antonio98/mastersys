package dev.jose.mastersys.fatura.repository;

import dev.jose.mastersys.fatura.domain.FaturaMatricula;
import dev.jose.mastersys.fatura.domain.enums.StatusFatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface FaturaMatriculaRepository extends JpaRepository<FaturaMatricula, Long>,
        JpaSpecificationExecutor<FaturaMatricula> {

     List<FaturaMatricula> findAllByStatusFaturaAndDataVencimentoBefore(StatusFatura statusFatura, LocalDate now);

     boolean existsByMatriculaIdAndDataVencimento(Long matriculaId, LocalDate dataVencimento);
}

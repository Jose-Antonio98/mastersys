package dev.jose.mastersys.matricula.repository;

import dev.jose.mastersys.matricula.domain.MatriculaModalidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatriculaModalidadeRepository extends JpaRepository<MatriculaModalidade, Long> {

    boolean existsByMatriculaIdAndModalidadeIdAndDataFimIsNull(Long matriculaId, Long modalidadeId);
    List<MatriculaModalidade> findAllByMatriculaId(Long matriculaId);

}

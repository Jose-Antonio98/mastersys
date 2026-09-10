package dev.jose.mastersys.matricula.repository;

import dev.jose.mastersys.matricula.domain.Matricula;
import dev.jose.mastersys.matricula.domain.enums.StatusMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MatriculaRepository extends JpaRepository<Matricula,Long>,
        JpaSpecificationExecutor<Matricula> {

    boolean existsByAlunoIdAndStatus(Long alunoId, StatusMatricula status);
}

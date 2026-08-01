package dev.jose.mastersys.repository;

import dev.jose.mastersys.domain.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatriculaRepository extends JpaRepository<Matricula,Long> {
}

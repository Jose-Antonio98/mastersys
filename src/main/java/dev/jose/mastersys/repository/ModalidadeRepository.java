package dev.jose.mastersys.repository;

import dev.jose.mastersys.domain.Modalidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModalidadeRepository extends JpaRepository<Modalidade,Long> {

    boolean existsByNomeIgnoreCase(String nome);
    List<Modalidade> findByAtivaTrue();
}

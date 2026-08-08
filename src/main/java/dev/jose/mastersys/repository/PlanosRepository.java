package dev.jose.mastersys.repository;

import dev.jose.mastersys.domain.Plano;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanosRepository extends JpaRepository<Plano,Long> {
    boolean existsByNomeAndModalidadeId(String nome, Long modalidadeId);
    boolean existsByNomeAndModalidadeIdAndIdNot(String nome, Long modalidadeId, Long id);
    List<Plano> findAllByModalidadeId(Long modadlidadeId);
}

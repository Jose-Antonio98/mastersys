package dev.jose.mastersys.repository;

import dev.jose.mastersys.domain.Modalidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ModalidadeRepository extends JpaRepository<Modalidade,Long> {

    @Query("""
    SELECT COUNT(m) > 0
    FROM Modalidade m
    WHERE LOWER(FUNCTION('unaccent', m.nome)) =
          LOWER(FUNCTION('unaccent', :nome))""")
    boolean existsByNomeIgnoreCaseAndAcentos(@Param("nome") String nome);

    @Query("""
    SELECT COUNT(m) > 0
    FROM Modalidade m
    WHERE LOWER(FUNCTION('unaccent', m.nome)) =
          LOWER(FUNCTION('unaccent', :nome))
    AND m.id <> :id""")
    boolean existsByNomeIgnoreCaseAndAcentosAndIdNot(@Param("nome") String nome, @Param("id") Long id);

    List<Modalidade> findByAtivaTrue();
}

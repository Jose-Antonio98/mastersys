package dev.jose.mastersys.repository;

import dev.jose.mastersys.domain.Plano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlanosRepository extends JpaRepository<Plano,Long> {
    @Query("""
    SELECT COUNT(p) > 0
    FROM Plano p
    WHERE LOWER(FUNCTION('unaccent', p.nome)) =
          LOWER(FUNCTION('unaccent', :nome))
    AND p.modalidade.id = :modalidadeId
    """)
    boolean existsByNomeIgnoreCaseAndAcentos(
            @Param("nome") String nome,
            @Param("modalidadeId") Long modalidadeId
    );

    @Query("""
    SELECT COUNT(p) > 0
    FROM Plano p
    WHERE LOWER(FUNCTION('unaccent', p.nome)) =
          LOWER(FUNCTION('unaccent', :nome))
    AND p.modalidade.id = :modalidadeId
    AND p.id <> :id
    """)
    boolean existsByNomeIgnoreCaseAndAcentosAndIdNot(
            @Param("nome") String nome,
            @Param("modalidadeId") Long modalidadeId,
            @Param("id") Long id
    );


    List<Plano> findAllByModalidadeId(Long modalidadeId);
}

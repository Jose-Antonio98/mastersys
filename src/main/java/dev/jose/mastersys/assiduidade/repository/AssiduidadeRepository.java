package dev.jose.mastersys.assiduidade.repository;

import dev.jose.mastersys.assiduidade.domain.Assiduidade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssiduidadeRepository extends JpaRepository<Assiduidade, Long> {
}

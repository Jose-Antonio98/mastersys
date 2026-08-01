package dev.jose.mastersys.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "graduacoes")
@Data
public class Graduacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modalidade_id")
    private Modalidade modalidade;


}

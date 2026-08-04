package dev.jose.mastersys.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "modalidades")
@Data
public class Modalidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(nullable = false)
    private Boolean ativa = true;

}

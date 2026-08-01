package dev.jose.mastersys.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "matriculas_modalidades")
@Data
public class MatriculaModalidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id")
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modalidade_id")
    private Modalidade modalidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graduacao_id")
    private Graduacao graduacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_id")
    private Plano plano;

    @PrePersist
    public void prePersist() {
        if (this.dataInicio == null) {
            this.dataInicio = LocalDate.now();
        }
    }
}

package dev.jose.mastersys.domain;

import dev.jose.mastersys.domain.enums.StatusMatricula;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "matriculas")
@Data
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "data_matricula")
    private LocalDate dataMatricula;

    @Column(name = "dia_vencimento")
    private Integer diaVencimento;

    @Column(name = "data_encerramento")
    private LocalDate dataEncerramento;

    @Enumerated(EnumType.STRING)
    private StatusMatricula status = StatusMatricula.ATIVA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @PrePersist
    public void prePersist(){
        if (dataMatricula == null){
            dataMatricula = LocalDate.now();
        }
    }
}

package dev.jose.mastersys.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "assiduidade")
@Data
public class Assiduidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "data_entrada")
    private LocalDateTime dataEntrada;

    @Column(name = "data_saida")
    private LocalDateTime dataSaida;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "matricula_id")
    private Matricula matricula;


    @PrePersist
    public void prePersist() {
        if (dataEntrada == null) {
            dataEntrada = LocalDateTime.now();
        }
    }
}

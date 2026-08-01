package dev.jose.mastersys.domain;

import dev.jose.mastersys.domain.enums.Statusfatura;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "faturas_matriculas")
@Data
public class FaturaMatricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    private BigDecimal valor;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "data_cancelamento")
    private LocalDate dataCancelamento;

    @Enumerated(EnumType.STRING)
    private Statusfatura  statusfatura = Statusfatura.ABERTA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id")
    private Matricula matricula;


}

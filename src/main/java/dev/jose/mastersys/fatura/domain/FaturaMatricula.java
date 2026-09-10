package dev.jose.mastersys.fatura.domain;

import dev.jose.mastersys.fatura.domain.enums.Statusfatura;
import dev.jose.mastersys.matricula.domain.Matricula;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "faturas_matriculas")
@Getter
@Setter
public class FaturaMatricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    private BigDecimal valor;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "data_cancelamento")
    private LocalDate dataCancelamento;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Statusfatura  statusfatura = Statusfatura.ABERTA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id")
    private Matricula matricula;


}

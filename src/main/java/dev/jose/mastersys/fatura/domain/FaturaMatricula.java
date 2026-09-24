package dev.jose.mastersys.fatura.domain;

import dev.jose.mastersys.fatura.domain.enums.StatusFatura;
import dev.jose.mastersys.fatura.exception.StatusFaturaInvalidoException;
import dev.jose.mastersys.matricula.domain.Matricula;
import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "data_cancelamento")
    private LocalDate dataCancelamento;

    @Setter(AccessLevel.NONE)
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusFatura statusFatura = StatusFatura.ABERTA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id")
    private Matricula matricula;

    public void pagar() {
        if (statusFatura != StatusFatura.ABERTA && statusFatura != StatusFatura.VENCIDA) {
            throw new StatusFaturaInvalidoException("Apenas faturas abertas ou vencidas podem ser pagas"
            );
        }

        statusFatura = StatusFatura.PAGA;
        dataPagamento = LocalDateTime.now();
    }

    public void cancelar() {
        if (statusFatura == StatusFatura.PAGA
                || statusFatura == StatusFatura.CANCELADA) {
            throw new StatusFaturaInvalidoException("A fatura não pode ser cancelada neste status.");
        }

        statusFatura = StatusFatura.CANCELADA;
        dataCancelamento = LocalDate.now();
    }

    public void marcarComoVencida() {
        if (statusFatura == StatusFatura.ABERTA && dataVencimento != null && dataVencimento.isBefore(LocalDate.now())) {
            statusFatura = StatusFatura.VENCIDA;
        }
    }
}

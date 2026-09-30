package dev.jose.mastersys.relatorio.controller;

import dev.jose.mastersys.fatura.domain.enums.StatusFatura;
import dev.jose.mastersys.relatorio.projection.AlunosPorCidadeProjection;
import dev.jose.mastersys.relatorio.projection.FaturaRelatorioProjection;
import dev.jose.mastersys.relatorio.projection.FaturamentoMensalProjection;
import dev.jose.mastersys.relatorio.service.RelatorioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {


    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/faturamento-mensal")
    public List<FaturamentoMensalProjection> faturamentoMensal() {
        return relatorioService.faturamentoMensal();
    }

    @GetMapping("/alunos-por-cidade")
    public List<AlunosPorCidadeProjection> alunosPorCidade(@RequestParam String cidade) {
        return relatorioService.alunosPorCidade(cidade);
    }

    @GetMapping("/faturas-em-aberto")
    public List<FaturaRelatorioProjection> faturasEmAberto() {
        return relatorioService.faturasEmAberto();
    }

    @GetMapping("/faturas-por-status")
    public List<FaturaRelatorioProjection> faturasPorStatus(@RequestParam StatusFatura status) {
        return relatorioService.faturaPorStatus(status);
    }
}

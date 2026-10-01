package dev.jose.mastersys.relatorio.service;

import dev.jose.mastersys.fatura.domain.enums.StatusFatura;
import dev.jose.mastersys.relatorio.projection.AlunosPorCidadeProjection;
import dev.jose.mastersys.relatorio.projection.FaturaRelatorioProjection;
import dev.jose.mastersys.relatorio.projection.FaturamentoMensalProjection;
import dev.jose.mastersys.relatorio.repository.RelatorioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelatorioService {

    private final RelatorioRepository relatorioAcademiaRepository;

    public RelatorioService(RelatorioRepository repository) {
        this.relatorioAcademiaRepository = repository;
    }

    public List<FaturamentoMensalProjection> faturamentoMensal() {
        return relatorioAcademiaRepository.faturamentoMensal();
    }

    public List<AlunosPorCidadeProjection> alunosPorCidade(String cidade) {
        return relatorioAcademiaRepository.alunosPorCidade(cidade);
    }

    public List<FaturaRelatorioProjection> faturasEmAberto() {
        return relatorioAcademiaRepository.faturasEmAberto();
    }

    public List<FaturaRelatorioProjection> faturaPorStatus(StatusFatura statusFatura) {
        return relatorioAcademiaRepository.faturasPorStatus(statusFatura.name());
    }
}

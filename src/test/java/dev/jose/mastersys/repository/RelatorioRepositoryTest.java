package dev.jose.mastersys.repository;

import dev.jose.mastersys.relatorio.projection.FaturaRelatorioProjection;
import dev.jose.mastersys.relatorio.repository.RelatorioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RelatorioRepositoryTest {

    @Autowired
    private RelatorioRepository relatorioRepository;

    @Test
    void deveExecutarConsultasDeRelatorioEMapearAsProjections() {
        var faturamentoMensal = relatorioRepository.faturamentoMensal();
        var alunosPorCidade = relatorioRepository.alunosPorCidade("Cruzeiro");
        var faturasEmAberto = relatorioRepository.faturasEmAberto();
        var faturasPagas = relatorioRepository.faturasPorStatus("PAGA");

        assertNotNull(faturamentoMensal);
        assertNotNull(alunosPorCidade);
        assertNotNull(faturasEmAberto);
        assertNotNull(faturasPagas);

        faturamentoMensal.forEach(faturamento -> {
            assertNotNull(faturamento.getMes());
            assertNotNull(faturamento.getTotal());
        });
        alunosPorCidade.forEach(alunos -> {
            assertNotNull(alunos.getCidade());
            assertNotNull(alunos.getQuantidade());
        });
        faturasEmAberto.forEach(this::validarProjectionDeFatura);
        faturasPagas.forEach(this::validarProjectionDeFatura);
    }

    private void validarProjectionDeFatura(FaturaRelatorioProjection fatura) {
        assertNotNull(fatura.getMatriculaId());
        assertNotNull(fatura.getAlunoNome());
        assertNotNull(fatura.getDataVencimento());
        assertNotNull(fatura.getValor());
        assertNotNull(fatura.getStatusFatura());
    }
}

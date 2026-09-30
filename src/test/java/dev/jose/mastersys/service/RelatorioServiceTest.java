package dev.jose.mastersys.service;

import dev.jose.mastersys.fatura.domain.enums.StatusFatura;
import dev.jose.mastersys.relatorio.projection.AlunosPorCidadeProjection;
import dev.jose.mastersys.relatorio.projection.FaturaRelatorioProjection;
import dev.jose.mastersys.relatorio.projection.FaturamentoMensalProjection;
import dev.jose.mastersys.relatorio.repository.RelatorioRepository;
import dev.jose.mastersys.relatorio.service.RelatorioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @Mock
    private RelatorioRepository relatorioRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    @Test
    void deveRetornarFaturamentoMensalDoRepositorio() {
        List<FaturamentoMensalProjection> esperado = List.of(mock(FaturamentoMensalProjection.class));
        when(relatorioRepository.faturamentoMensal()).thenReturn(esperado);

        assertSame(esperado, relatorioService.faturamentoMensal());

        verify(relatorioRepository).faturamentoMensal();
        verifyNoMoreInteractions(relatorioRepository);
    }

    @Test
    void deveBuscarAlunosPelaCidadeInformada() {
        List<AlunosPorCidadeProjection> esperado = List.of(mock(AlunosPorCidadeProjection.class));
        when(relatorioRepository.alunosPorCidade("Cruzeiro")).thenReturn(esperado);

        assertSame(esperado, relatorioService.alunosPorCidade("Cruzeiro"));

        verify(relatorioRepository).alunosPorCidade("Cruzeiro");
        verifyNoMoreInteractions(relatorioRepository);
    }

    @Test
    void deveRetornarFaturasEmAbertoDoRepositorio() {
        List<FaturaRelatorioProjection> esperado = List.of(mock(FaturaRelatorioProjection.class));
        when(relatorioRepository.faturasEmAberto()).thenReturn(esperado);

        assertSame(esperado, relatorioService.faturasEmAberto());

        verify(relatorioRepository).faturasEmAberto();
        verifyNoMoreInteractions(relatorioRepository);
    }

    @Test
    void deveBuscarFaturasPeloStatusInformado() {
        List<FaturaRelatorioProjection> esperado = List.of(mock(FaturaRelatorioProjection.class));
        when(relatorioRepository.faturasPorStatus("PAGA")).thenReturn(esperado);

        assertSame(esperado, relatorioService.faturaPorStatus(StatusFatura.PAGA));

        verify(relatorioRepository).faturasPorStatus("PAGA");
        verifyNoMoreInteractions(relatorioRepository);
    }
}

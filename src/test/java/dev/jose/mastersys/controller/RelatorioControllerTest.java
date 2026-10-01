package dev.jose.mastersys.controller;

import dev.jose.mastersys.fatura.domain.enums.StatusFatura;
import dev.jose.mastersys.relatorio.controller.RelatorioController;
import dev.jose.mastersys.relatorio.projection.AlunosPorCidadeProjection;
import dev.jose.mastersys.relatorio.projection.FaturaRelatorioProjection;
import dev.jose.mastersys.relatorio.projection.FaturamentoMensalProjection;
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
class RelatorioControllerTest {

    @Mock
    private RelatorioService service;

    @InjectMocks
    private RelatorioController controller;

    @Test
    void deveDelegarConsultasDosRelatorios() {
        var faturamento = List.of(mock(FaturamentoMensalProjection.class));
        var alunos = List.of(mock(AlunosPorCidadeProjection.class));
        var faturas = List.of(mock(FaturaRelatorioProjection.class));
        when(service.faturamentoMensal()).thenReturn(faturamento);
        when(service.alunosPorCidade("Cruzeiro")).thenReturn(alunos);
        when(service.faturasEmAberto()).thenReturn(faturas);
        when(service.faturaPorStatus(StatusFatura.PAGA)).thenReturn(faturas);

        assertSame(faturamento, controller.faturamentoMensal());
        assertSame(alunos, controller.alunosPorCidade("Cruzeiro"));
        assertSame(faturas, controller.faturasEmAberto());
        assertSame(faturas, controller.faturasPorStatus(StatusFatura.PAGA));

        verify(service).faturamentoMensal();
        verify(service).alunosPorCidade("Cruzeiro");
        verify(service).faturasEmAberto();
        verify(service).faturaPorStatus(StatusFatura.PAGA);
        verifyNoMoreInteractions(service);
    }
}

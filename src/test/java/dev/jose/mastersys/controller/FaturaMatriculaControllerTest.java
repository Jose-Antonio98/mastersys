package dev.jose.mastersys.controller;

import dev.jose.mastersys.fatura.controller.FaturaMatriculaController;
import dev.jose.mastersys.fatura.domain.enums.StatusFatura;
import dev.jose.mastersys.fatura.dto.FaturaFiltroRequest;
import dev.jose.mastersys.fatura.dto.FaturaMatriculaRequest;
import dev.jose.mastersys.fatura.dto.FaturaMatriculaResponse;
import dev.jose.mastersys.fatura.service.FaturaMatriculaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FaturaMatriculaControllerTest {

    @Mock
    private FaturaMatriculaService service;

    @InjectMocks
    private FaturaMatriculaController controller;

    @Test
    void deveDelegarCriacaoConsultaEListagemDeFaturas() {
        var request = new FaturaMatriculaRequest(1L);
        var filtro = new FaturaFiltroRequest(1L, StatusFatura.ABERTA, null, null);
        var pageable = PageRequest.of(0, 10);
        var response = response();
        var pagina = Page.<FaturaMatriculaResponse>empty(pageable);
        when(service.criarFatura(request)).thenReturn(response);
        when(service.buscarFaturaPorId(2L)).thenReturn(response);
        when(service.listarFaturas(filtro, pageable)).thenReturn(pagina);

        assertSame(response, controller.cadastrarFatura(request));
        assertSame(response, controller.buscarFaturaPorId(2L));
        assertSame(pagina, controller.buscarFaturas(filtro, pageable));

        verify(service).criarFatura(request);
        verify(service).buscarFaturaPorId(2L);
        verify(service).listarFaturas(filtro, pageable);
        verifyNoMoreInteractions(service);
    }

    @Test
    void deveDelegarPagamentoECancelamento() {
        var response = response();
        when(service.pagarFatura(2L)).thenReturn(response);

        assertSame(response, controller.pagarFatura(2L));
        controller.cancelarFatura(2L);

        verify(service).pagarFatura(2L);
        verify(service).cancelarFatura(2L);
        verifyNoMoreInteractions(service);
    }

    private FaturaMatriculaResponse response() {
        return new FaturaMatriculaResponse(2L, null, new BigDecimal("120.00"),
                null, null, StatusFatura.ABERTA, 1L);
    }
}

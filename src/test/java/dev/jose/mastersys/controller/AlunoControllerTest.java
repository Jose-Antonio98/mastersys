package dev.jose.mastersys.controller;

import dev.jose.mastersys.aluno.controller.AlunoController;
import dev.jose.mastersys.aluno.dto.AlunoAtualizacaoRequest;
import dev.jose.mastersys.aluno.dto.AlunoFiltroRequest;
import dev.jose.mastersys.aluno.dto.AlunoResponse;
import dev.jose.mastersys.aluno.service.AlunoService;
import dev.jose.mastersys.factory.AlunoTestFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoControllerTest {

    @Mock
    private AlunoService alunoService;

    @InjectMocks
    private AlunoController controller;

    @Test
    void deveDelegarOperacoesDeConsultaEListagem() {
        var filtro = new AlunoFiltroRequest("Ana", null, null, null, null);
        var pageable = PageRequest.of(0, 10);
        var pagina = Page.<AlunoResponse>empty(pageable);
        var response = response();
        when(alunoService.listar(filtro, pageable)).thenReturn(pagina);
        when(alunoService.buscarPorId(1L)).thenReturn(response);

        assertSame(pagina, controller.listar(filtro, pageable));
        assertSame(response, controller.buscarPorId(1L));

        verify(alunoService).listar(filtro, pageable);
        verify(alunoService).buscarPorId(1L);
        verifyNoMoreInteractions(alunoService);
    }

    @Test
    void deveDelegarCriacaoAtualizacaoEExclusao() {
        var request = AlunoTestFactory.alunoRequest();
        var parcial = new AlunoAtualizacaoRequest(null, null, null, null, null,
                null, "Observação", null, null, null, null, null, null, null);
        var response = response();
        when(alunoService.cadastrar(request)).thenReturn(response);
        when(alunoService.atualizar(1L, request)).thenReturn(response);
        when(alunoService.atualizarParcial(1L, parcial)).thenReturn(response);

        assertSame(response, controller.cadastrar(request));
        assertSame(response, controller.atualizar(1L, request));
        assertSame(response, controller.atualizarParcial(1L, parcial));
        controller.delete(1L);

        verify(alunoService).cadastrar(request);
        verify(alunoService).atualizar(1L, request);
        verify(alunoService).atualizarParcial(1L, parcial);
        verify(alunoService).excluir(1L);
        verifyNoMoreInteractions(alunoService);
    }

    private AlunoResponse response() {
        var aluno = AlunoTestFactory.alunoRequest().toEntity();
        aluno.setId(1L);
        return AlunoResponse.fromEntity(aluno);
    }
}

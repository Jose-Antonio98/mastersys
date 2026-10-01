package dev.jose.mastersys.controller;

import dev.jose.mastersys.factory.ModalidadeTestFactory;
import dev.jose.mastersys.modalidade.controller.ModalidadeController;
import dev.jose.mastersys.modalidade.dto.ModalidadeResponse;
import dev.jose.mastersys.modalidade.service.ModalidadeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModalidadeControllerTest {

    @Mock
    private ModalidadeService modalidadeService;

    @InjectMocks
    private ModalidadeController controller;

    @Test
    void deveDelegarOperacoesDeConsulta() {
        var modalidade = ModalidadeTestFactory.modalidadeRequest().toEntity();
        modalidade.setId(1L);
        var response = ModalidadeResponse.fromEntity(modalidade);
        var todas = List.of(response);
        var ativas = List.of(response);
        when(modalidadeService.buscarPorId(1L)).thenReturn(response);
        when(modalidadeService.listarModalidades()).thenReturn(todas);
        when(modalidadeService.listarModalidadesAtivas()).thenReturn(ativas);

        assertSame(response, controller.buscarPorId(1L));
        assertSame(todas, controller.listar());
        assertSame(ativas, controller.listarDisponiveis());

        verify(modalidadeService).buscarPorId(1L);
        verify(modalidadeService).listarModalidades();
        verify(modalidadeService).listarModalidadesAtivas();
        verifyNoMoreInteractions(modalidadeService);
    }

    @Test
    void deveDelegarCadastroAtualizacaoEAlteracoesDeStatus() {
        var request = ModalidadeTestFactory.modalidadeRequest();
        var modalidade = request.toEntity();
        modalidade.setId(1L);
        var response = ModalidadeResponse.fromEntity(modalidade);
        when(modalidadeService.cadastrarModalidade(request)).thenReturn(response);
        when(modalidadeService.atualizarModalidade(1L, request)).thenReturn(response);

        assertSame(response, controller.cadastrar(request));
        assertSame(response, controller.atualizar(1L, request));
        controller.ativarModalidade(1L);
        controller.inativarModalidade(1L);
        controller.deletar(1L);

        verify(modalidadeService).cadastrarModalidade(request);
        verify(modalidadeService).atualizarModalidade(1L, request);
        verify(modalidadeService).ativarModalidade(1L);
        verify(modalidadeService).inativarModalidade(1L);
        verify(modalidadeService).removerModalidade(1L);
        verifyNoMoreInteractions(modalidadeService);
    }
}

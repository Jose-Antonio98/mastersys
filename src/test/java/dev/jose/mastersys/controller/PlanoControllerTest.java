package dev.jose.mastersys.controller;

import dev.jose.mastersys.factory.ModalidadeTestFactory;
import dev.jose.mastersys.factory.PlanoAtualizacaoRequestBuilder;
import dev.jose.mastersys.factory.PlanoTestFactory;
import dev.jose.mastersys.plano.controller.PlanoController;
import dev.jose.mastersys.plano.dto.PlanoResponse;
import dev.jose.mastersys.plano.service.PlanoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanoControllerTest {

    @Mock
    private PlanoService planoService;

    @InjectMocks
    private PlanoController controller;

    @Test
    void deveDelegarOperacoesDeConsulta() {
        var modalidade = ModalidadeTestFactory.modalidadeRequest().toEntity();
        modalidade.setId(1L);
        var plano = PlanoTestFactory.planoRequest().toEntity();
        plano.setId(2L);
        plano.setModalidade(modalidade);
        var response = PlanoResponse.fromEntity(plano);
        var lista = List.of(response);
        when(planoService.buscarPlanoPorId(2L)).thenReturn(response);
        when(planoService.listarPlanosPorModalidade(1L)).thenReturn(lista);

        assertSame(response, controller.buscarPlanoPorId(2L));
        assertSame(lista, controller.buscarPlanos(1L));

        verify(planoService).buscarPlanoPorId(2L);
        verify(planoService).listarPlanosPorModalidade(1L);
        verifyNoMoreInteractions(planoService);
    }

    @Test
    void deveDelegarCadastroAtualizacaoEAlteracoesDeStatus() {
        var request = PlanoTestFactory.planoRequest();
        var atualizacao = new PlanoAtualizacaoRequestBuilder().nome("Anual").build();
        var modalidade = ModalidadeTestFactory.modalidadeRequest().toEntity();
        modalidade.setId(1L);
        var plano = request.toEntity();
        plano.setId(2L);
        plano.setModalidade(modalidade);
        var response = PlanoResponse.fromEntity(plano);
        when(planoService.cadastrarPlano(request)).thenReturn(response);
        when(planoService.atualizarPlano(2L, atualizacao)).thenReturn(response);

        assertSame(response, controller.cadastrarPlano(request));
        assertSame(response, controller.atualizarPlano(2L, atualizacao));
        controller.ativarPlano(2L);
        controller.inativarPlano(2L);
        controller.removerPlano(2L);

        verify(planoService).cadastrarPlano(request);
        verify(planoService).atualizarPlano(2L, atualizacao);
        verify(planoService).ativarPlano(2L);
        verify(planoService).inativarPlano(2L);
        verify(planoService).removerPlano(2L);
        verifyNoMoreInteractions(planoService);
    }
}

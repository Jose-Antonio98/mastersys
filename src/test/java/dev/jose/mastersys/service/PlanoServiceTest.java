package dev.jose.mastersys.service;



import dev.jose.mastersys.exception.RecursoJaAtivoException;
import dev.jose.mastersys.exception.RecursoJaInativoException;
import dev.jose.mastersys.modalidade.domain.Modalidade;
import dev.jose.mastersys.plano.domain.Plano;
import dev.jose.mastersys.modalidade.exception.ModalidadeNaoEncontradaException;
import dev.jose.mastersys.plano.exception.PlanoNaoEncontradoException;
import dev.jose.mastersys.exception.RecursoJaCadastradoException;
import dev.jose.mastersys.factory.ModalidadeTestFactory;
import dev.jose.mastersys.factory.PlanoAtualizacaoRequestBuilder;
import dev.jose.mastersys.factory.PlanoTestFactory;
import dev.jose.mastersys.modalidade.repository.ModalidadeRepository;
import dev.jose.mastersys.plano.repository.PlanosRepository;
import dev.jose.mastersys.plano.service.PlanoService;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlanoServiceTest {

    @Mock
    private PlanosRepository planosRepository;

    @Mock
    private ModalidadeRepository modalidadeRepository;

    @InjectMocks
    private PlanoService planoService;

    // ==========================
    // CADASTRO
    // ==========================

    @Test
    void deveriaCriarPlanoComSucesso() {

        //given
        var request = PlanoTestFactory.planoRequest();
        var modalidade = criarModalidade();

        when(modalidadeRepository.findById(request.modalidadeId())).thenReturn(Optional.of(modalidade));

        when(planosRepository.existsByNomeIgnoreCaseAndAcentos(request.nome(), request.modalidadeId()))
                .thenReturn(false);

        when(planosRepository.save(any(Plano.class))).thenAnswer(i -> i.getArgument(0));

        //when
        var response = planoService.cadastrarPlano(request);

        //then
        assertNotNull(response);
        assertEquals(request.nome(), response.nome());
        assertEquals(request.modalidadeId(), response.modalidadeId());
        assertEquals(request.valor(), response.valor());

        verify(modalidadeRepository, times(1)).findById(request.modalidadeId());
        verify(planosRepository).existsByNomeIgnoreCaseAndAcentos(request.nome(), request.modalidadeId());

        ArgumentCaptor<Plano> captor = ArgumentCaptor.forClass(Plano.class);

        verify(planosRepository).save(captor.capture());

        assertEquals(response.id(), captor.getValue().getId());
        assertEquals(response.nome(), captor.getValue().getNome());
        assertEquals(response.modalidadeId(), captor.getValue().getModalidade().getId());
        assertEquals(response.valor(), captor.getValue().getValorMensal());

        verifyNoMoreInteractions(planosRepository, modalidadeRepository);
    }

    @Test
    void deveLancarExcecaoComModalidadeNaoEncontrado() {

        //given
        var request = PlanoTestFactory.planoRequest();

        when(modalidadeRepository.findById(request.modalidadeId())).thenReturn(Optional.empty());

        //when
        assertThrows(ModalidadeNaoEncontradaException.class, () -> planoService.cadastrarPlano(request));

        //then
        verify(modalidadeRepository).findById(request.modalidadeId());
        verifyNoMoreInteractions(planosRepository, modalidadeRepository);
    }

    @Test
    void deveLancarExcecaoComPlanoJaCadastrado() {

        //given
        var request = PlanoTestFactory.planoRequest();
        var modalidade = criarModalidade();

        when(modalidadeRepository.findById(request.modalidadeId())).thenReturn(Optional.of(modalidade));

        when(planosRepository.existsByNomeIgnoreCaseAndAcentos(request.nome(), request.modalidadeId()))
                .thenReturn(true);

        //when
        assertThrows(RecursoJaCadastradoException.class, () -> planoService.cadastrarPlano(request));

        //then
        verify(modalidadeRepository, times(1)).findById(request.modalidadeId());
        verify(planosRepository).existsByNomeIgnoreCaseAndAcentos(request.nome(), request.modalidadeId());
        verify(planosRepository, never()).save(any(Plano.class));
        verifyNoMoreInteractions(planosRepository,  modalidadeRepository);
    }

    // ==========================
    // ATUALIZAÇÃO
    // ==========================

    @Test
    void deveAtualizarNomeDoPlanoComSucesso() {

        //given
        var plano = criarPlano();

        var planoOriginal = criarPlano();

        when(planosRepository.findById(plano.getId())).thenReturn(Optional.of(plano));
        when(planosRepository.save(any(Plano.class))).thenAnswer(i -> i.getArgument(0));

        var request = new PlanoAtualizacaoRequestBuilder().nome("Anual").build();

        when(planosRepository.existsByNomeIgnoreCaseAndAcentosAndIdNot
                (request.nome(), plano.getModalidade().getId(), plano.getId())).thenReturn(false);

        //when
        var response = planoService.atualizarPlano(plano.getId(), request);

        //then
        assertNotNull(response);
        assertEquals(plano.getId(), response.id());
        assertEquals(request.nome(), response.nome());
        assertEquals(planoOriginal.getValorMensal(), response.valor());

        verify(planosRepository, times(1)).findById(plano.getId());
        ArgumentCaptor<Plano> captor = ArgumentCaptor.forClass(Plano.class);
        verify(planosRepository).save(captor.capture());

        assertEquals(planoOriginal.getId(), captor.getValue().getId());
        assertEquals("Anual", captor.getValue().getNome());
        assertEquals(planoOriginal.getValorMensal(), captor.getValue().getValorMensal());
        assertEquals(planoOriginal.getModalidade().getId(), captor.getValue().getModalidade().getId());
        assertEquals(planoOriginal.getModalidade().getNome(), captor.getValue().getModalidade().getNome());

        verify(planosRepository).existsByNomeIgnoreCaseAndAcentosAndIdNot
                (request.nome(), plano.getModalidade().getId(), plano.getId());

        verifyNoMoreInteractions(planosRepository);
    }

    @Test
    void deveAtualizarNomeEValorDoPlanoComSucesso() {

        //given
        var plano = criarPlano();

        var planoOriginal = criarPlano();

        when(planosRepository.findById(plano.getId())).thenReturn(Optional.of(plano));
        when(planosRepository.save(any(Plano.class))).thenAnswer(i -> i.getArgument(0));

        var request = new PlanoAtualizacaoRequestBuilder().nome("Anual").valor(new BigDecimal("200.00")).build();

        when(planosRepository.existsByNomeIgnoreCaseAndAcentosAndIdNot
                (request.nome(), plano.getModalidade().getId(), plano.getId())).thenReturn(false);

        //when
        var response = planoService.atualizarPlano(plano.getId(), request);

        //then
        assertNotNull(response);
        assertEquals(plano.getId(), response.id());
        assertEquals(request.nome(), response.nome());
        assertEquals(request.valor(), response.valor());

        verify(planosRepository, times(1)).findById(plano.getId());
        ArgumentCaptor<Plano> captor = ArgumentCaptor.forClass(Plano.class);
        verify(planosRepository).save(captor.capture());

        assertEquals(planoOriginal.getId(), captor.getValue().getId());
        assertEquals(request.nome(), captor.getValue().getNome());
        assertEquals(request.valor(), captor.getValue().getValorMensal());
        assertEquals(planoOriginal.getModalidade().getId(), captor.getValue().getModalidade().getId());
        assertEquals(planoOriginal.getModalidade().getNome(), captor.getValue().getModalidade().getNome());

        verify(planosRepository).existsByNomeIgnoreCaseAndAcentosAndIdNot
                (request.nome(), plano.getModalidade().getId(), plano.getId());

        verifyNoMoreInteractions(planosRepository);
    }

    @Test
    void deveLancarPlanoNaoEncontradoException() {

        //given
        var request = new PlanoAtualizacaoRequestBuilder().nome("Anual").valor(new BigDecimal("200.00")).build();

        when(planosRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        assertThrows(PlanoNaoEncontradoException.class, () -> planoService.atualizarPlano(1L, request));

        //then
        verify(planosRepository, times(1)).findById(1L);
        verify(planosRepository, never()).save(any(Plano.class));
        verify(planosRepository, never()).existsByNomeIgnoreCaseAndAcentosAndIdNot(any(), any(), any());
        verifyNoMoreInteractions(planosRepository);
    }

    @Test
    void deveLancarRecursoJaCadastradoExceptionAoAtualizarNomeDuplicado() {

        //given
        var plano = criarPlano();

        when(planosRepository.findById(plano.getId())).thenReturn(Optional.of(plano));

        var request = new PlanoAtualizacaoRequestBuilder().nome("Anual").valor(new BigDecimal("200.00")).build();

        when(planosRepository.existsByNomeIgnoreCaseAndAcentosAndIdNot
                (request.nome(), plano.getModalidade().getId(), plano.getId())).thenReturn(true);

        //when
        assertThrows(RecursoJaCadastradoException.class, () -> planoService.atualizarPlano(plano.getId(), request));

        //then
        verify(planosRepository, times(1)).findById(plano.getId());

        verify(planosRepository).existsByNomeIgnoreCaseAndAcentosAndIdNot(
                request.nome(), plano.getModalidade().getId(), plano.getId());

        verify(planosRepository, never()).save(any(Plano.class));
        verifyNoMoreInteractions(planosRepository);
    }

    // ==========================
    // Buscar
    // ==========================

    @Test
    void deveBuscarPlanoPorIdComSucesso() {

        //given
        var plano = criarPlano();

        when(planosRepository.findById(plano.getId())).thenReturn(Optional.of(plano));

        //when
        var response = planoService.buscarPlanoPorId(plano.getId());

        //then
        assertNotNull(response);
        assertEquals(plano.getId(), response.id());
        assertEquals(plano.getNome(), response.nome());
        assertEquals(plano.getValorMensal(), response.valor());
        assertEquals(plano.getModalidade().getId(), response.modalidadeId());
        verify(planosRepository, times(1)).findById(plano.getId());

    }

    @Test
    void deveLancarPlanoNaoEncontradoExceptionAoBuscarPlanoPorId() {

        //given

        when(planosRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        assertThrows(PlanoNaoEncontradoException.class, () -> planoService.buscarPlanoPorId(1L));

        //then

        verify(planosRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(planosRepository);
    }

    @Test
    void deveListarPlanosDaModalidade() {

        //given
        var plano = criarPlano();
        var plano2 = criarPlanoDiferenteComId(2L, "Trimestral");
        var plano3 = criarPlanoDiferenteComId(3L, "anual");

        var modalidadeId = plano.getModalidade().getId();

        when(modalidadeRepository.findById(modalidadeId))
                .thenReturn(Optional.of(plano.getModalidade()));

        when(planosRepository.findAllByModalidadeId(modalidadeId)).thenReturn(Arrays.asList(plano, plano2, plano3));

        //when
        var response = planoService.listarPlanosPorModalidade(modalidadeId);

        //then
        assertNotNull(response);
        assertEquals(3, response.size());
        assertEquals(1L, response.get(0).id());
        assertEquals(2L, response.get(1).id());
        assertEquals(3L, response.get(2).id());
        assertEquals(plano.getNome(), response.get(0).nome());
        assertEquals(plano2.getNome(), response.get(1).nome());
        assertEquals(plano3.getNome(), response.get(2).nome());
        assertEquals(modalidadeId, response.get(0).modalidadeId());
        assertEquals(modalidadeId, response.get(1).modalidadeId());
        assertEquals(modalidadeId, response.get(2).modalidadeId());


        verify(modalidadeRepository, times(1)).findById(plano.getModalidade().getId());
        verify(planosRepository, times(1)).findAllByModalidadeId(modalidadeId);
        verifyNoMoreInteractions(planosRepository, modalidadeRepository);
    }

    @Test
    void deveRetornarListaSemPlanosDaModalidade() {

        //given
        when(modalidadeRepository.findById(1L)).thenReturn(Optional.of(criarModalidade()));
        when(planosRepository.findAllByModalidadeId(1L)).thenReturn(List.of());

        //when
        var response = planoService.listarPlanosPorModalidade(1L);

        //then
        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(modalidadeRepository).findById(1L);
        verifyNoMoreInteractions(planosRepository,  modalidadeRepository);
    }

    @Test
    void deveLancarModalidadeNaoEncontradoExceptionAoBuscarPlanosPorModalidadeId() {

        //given
        when(modalidadeRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        assertThrows(ModalidadeNaoEncontradaException.class, () ->
                planoService.listarPlanosPorModalidade(1L));

        //then
        verify(modalidadeRepository, times(1)).findById(1L);
        verify(planosRepository, never()).findAllByModalidadeId(1L);
        verifyNoMoreInteractions(planosRepository, modalidadeRepository);
    }

    // ==========================
    // ATIVAÇÃO/DESATIVAÇÃO
    // ==========================

    @Test
    void deveInativarPlanoComSucesso() {
        //given
        var plano = criarPlano();

        when(planosRepository.findById(plano.getId())).thenReturn(Optional.of(plano));

        //when
        planoService.inativarPlano(plano.getId());

        //then
        assertFalse(plano.getAtivo());
        verify(planosRepository, times(1)).findById(plano.getId());
        verifyNoMoreInteractions(planosRepository);
    }

    @Test
    void deveLancarPlanoNaoEncontradoExceptionAoInativarPlano() {
        //given
        when(planosRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        assertThrows(PlanoNaoEncontradoException.class, () -> planoService.inativarPlano(1L));

        //then
        verify(planosRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(planosRepository);
    }

    @Test
    void deveLancarRecursoJaInativoException() {
        //given
        var plano = criarPlano();
        plano.setAtivo(false);

        when(planosRepository.findById(plano.getId())).thenReturn(Optional.of(plano));

        //when
        assertThrows(RecursoJaInativoException.class, () -> planoService.inativarPlano(plano.getId()));

        //then
        verify(planosRepository, times(1)).findById(plano.getId());
        verifyNoMoreInteractions(planosRepository);
    }

    @Test
    void deveAtivarPlanoComSucesso() {
        //given
        var plano = criarPlano();
        plano.setAtivo(false);

        when(planosRepository.findById(plano.getId())).thenReturn(Optional.of(plano));

        //when
        planoService.ativarPlano(plano.getId());

        //then
        assertTrue(plano.getAtivo());
        verify(planosRepository, times(1)).findById(plano.getId());
        verifyNoMoreInteractions(planosRepository);
    }

    @Test
    void deveLancarPlanoNaoEncontradoExceptionAoAtivarPlano() {
        //given
        when(planosRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        assertThrows(PlanoNaoEncontradoException.class, () -> planoService.ativarPlano(1L));

        //then
        verify(planosRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(planosRepository);
    }

    @Test
    void deveLancarRecursoJaAtivoException() {
        //given
        var plano = criarPlano();

        when(planosRepository.findById(plano.getId())).thenReturn(Optional.of(plano));

        //when
        assertThrows(RecursoJaAtivoException.class, () -> planoService.ativarPlano(plano.getId()));

        //then
        verify(planosRepository, times(1)).findById(plano.getId());
        verifyNoMoreInteractions(planosRepository);
    }

    // ==========================
    // EXCLUSÃO
    // ==========================

    @Test
    void deveRemoverPlanoComSucesso() {
        //given
        var plano = criarPlano();
        when(planosRepository.findById(plano.getId())).thenReturn(Optional.of(plano));

        //when
        planoService.removerPlano(plano.getId());

        //then
        verify(planosRepository).findById(plano.getId());
        verify(planosRepository).delete(plano);
        verifyNoMoreInteractions(planosRepository);
    }

    @Test
    void deveLancarPlanoNaoEncontradoExceptionAoRemoverPlano() {
        //given
        when(planosRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        assertThrows(PlanoNaoEncontradoException.class, () -> planoService.removerPlano(1L));

        //then
        verify(planosRepository).findById(1L);
        verifyNoMoreInteractions(planosRepository);
    }

    // ==========================
    // PRIVADOS
    // ==========================

    private static @NonNull Plano criarPlano() {
        var plano = PlanoTestFactory.planoRequest().toEntity();
        plano.setId(1L);
        plano.setModalidade(criarModalidade());
        return plano;
    }

    private static @NonNull Plano criarPlanoComId(Long id) {
        var plano = PlanoTestFactory.planoRequest().toEntity();
        plano.setId(id);
        return plano;
    }

    private static @NonNull Plano criarPlanoDiferenteComId(Long id, String nome) {
        var plano = PlanoTestFactory.planoRequest().toEntity();
        plano.setId(id);
        plano.setNome(nome);
        plano.setModalidade(criarModalidade());
        return plano;
    }

    private static @NonNull Modalidade criarModalidade() {
        var modalidade = ModalidadeTestFactory.modalidadeRequest().toEntity();
        modalidade.setId(1L);
        return modalidade;
    }
}

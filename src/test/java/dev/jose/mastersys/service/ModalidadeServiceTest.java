package dev.jose.mastersys.service;

import dev.jose.mastersys.domain.Modalidade;
import dev.jose.mastersys.exception.ModalidadeNaoEncontradaException;
import dev.jose.mastersys.exception.RecursoJaAtivoException;
import dev.jose.mastersys.exception.RecursoJaCadastradoException;
import dev.jose.mastersys.exception.RecursoJaInativoException;
import dev.jose.mastersys.factory.ModalidadeTestFactory;
import dev.jose.mastersys.repository.ModalidadeRepository;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModalidadeServiceTest {

    @Mock
    private ModalidadeRepository modalidadeRepository;

    @InjectMocks
    private ModalidadeService modalidadeService;

    // ==========================
    // CADASTRO
    // ==========================
    @Test
    void deveCadastrarModalidade() {

        //given
        var request = ModalidadeTestFactory.modalidadeRequest();

        when(modalidadeRepository.save(any(Modalidade.class))).thenAnswer(i -> {
            Modalidade modalidade = (Modalidade) i.getArguments()[0];
            modalidade.setId(1L);
            return modalidade;
        });

        when(modalidadeRepository.existsByNomeIgnoreCaseAndAcentos(request.nome())).thenReturn(false);

        //when

        var response = modalidadeService.cadastrarModalidade(request);


        //then

        assertNotNull(response);

        assertEquals(1L, response.id());
        assertEquals(request.nome(), response.nome());
        assertTrue(response.ativa());

        ArgumentCaptor<Modalidade> modalidadeAtualizada = ArgumentCaptor.forClass(Modalidade.class);

        verify(modalidadeRepository).save(modalidadeAtualizada.capture());

        assertEquals(1L, modalidadeAtualizada.getValue().getId());
        assertEquals(request.nome(), modalidadeAtualizada.getValue().getNome());
        assertTrue(modalidadeAtualizada.getValue().getAtiva());

        verify(modalidadeRepository).existsByNomeIgnoreCaseAndAcentos(request.nome());
        verifyNoMoreInteractions(modalidadeRepository);

    }

    @Test
    void deveImpedirModalidadeDuplicada() {

        //given
        var request = ModalidadeTestFactory.modalidadeRequest();

        when(modalidadeRepository.existsByNomeIgnoreCaseAndAcentos(request.nome())).thenReturn(true);

        //when
        assertThrows(RecursoJaCadastradoException.class, () -> modalidadeService.cadastrarModalidade(request));

        //then
        verify(modalidadeRepository).existsByNomeIgnoreCaseAndAcentos(request.nome());
        verify(modalidadeRepository, never()).save(any(Modalidade.class));
        verifyNoMoreInteractions(modalidadeRepository);
    }

    // ==========================
    // ATUALIZAÇÃO
    // ==========================

    @Test
    void deveAtualizarModalidade() {

        //given
        var modalidade = criarModalidade();

        when(modalidadeRepository.findById(1L)).thenReturn(Optional.of(modalidade));
        when(modalidadeRepository.save(any(Modalidade.class))).thenReturn(modalidade);

        var request = ModalidadeTestFactory.modalidadeRequestAtualizado();

        when(modalidadeRepository.existsByNomeIgnoreCaseAndAcentosAndIdNot(request.nome(), 1L))
                .thenReturn(false);

        //when
        var response = modalidadeService.atualizarModalidade(1L, request);

        //then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(request.nome(), response.nome());
        assertTrue(response.ativa());

        verify(modalidadeRepository).findById(1L);
        ArgumentCaptor<Modalidade> modalidadeAtualizada = ArgumentCaptor.forClass(Modalidade.class);
        verify(modalidadeRepository).save(modalidadeAtualizada.capture());

        assertEquals(1L, modalidadeAtualizada.getValue().getId());
        assertEquals(request.nome(), modalidadeAtualizada.getValue().getNome());
        assertTrue(modalidadeAtualizada.getValue().getAtiva());
        verify(modalidadeRepository).existsByNomeIgnoreCaseAndAcentosAndIdNot(response.nome(), 1L);
        verifyNoMoreInteractions(modalidadeRepository);
    }

    @Test
    void deveImpedirAtualizacaoComNomeDuplicado() {

        //given
        var modalidade = criarModalidade();

        when(modalidadeRepository.findById(1L)).thenReturn(Optional.of(modalidade));

        var request = ModalidadeTestFactory.modalidadeRequestAtualizado();

        when(modalidadeRepository.existsByNomeIgnoreCaseAndAcentosAndIdNot(request.nome(), 1L))
                .thenReturn(true);

        //when
        assertThrows(RecursoJaCadastradoException.class, () -> modalidadeService.atualizarModalidade(1L, request));

        //then
        verify(modalidadeRepository).findById(1L);
        verify(modalidadeRepository, never()).save(any(Modalidade.class));
        verify(modalidadeRepository).existsByNomeIgnoreCaseAndAcentosAndIdNot(request.nome(), 1L);
        verifyNoMoreInteractions(modalidadeRepository);
    }

    // ==========================
    // BUSCA
    // ==========================

    @Test
    void deveBuscarModalidadePorId() {

        //given
        var modalidade = criarModalidade();

        when(modalidadeRepository.findById(1L)).thenReturn(Optional.of(modalidade));

        //when
        var request = modalidadeService.buscarPorId(1L);

        //then
        assertNotNull(request);
        assertEquals(1L, request.id());
        assertEquals(modalidade.getNome(), request.nome());
        assertEquals(modalidade.getAtiva(), request.ativa());

        verify(modalidadeRepository).findById(1L);
        verifyNoMoreInteractions(modalidadeRepository);
    }

    @Test
    void deveLancarExcecaoQuandoModalidadeNaoExistir() {

        //given
        when(modalidadeRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        assertThrows(ModalidadeNaoEncontradaException.class, () -> modalidadeService.buscarPorId(1L));

        //then
        verify(modalidadeRepository).findById(1L);
        verifyNoMoreInteractions(modalidadeRepository);
    }

    @Test
    void deveBuscarTodasModalidades() {

        //given
        var modalidade = criarModalidadeComId(1L);
        var modalidade2 = criarModalidadeComId(2L);
        var modalidade3 = criarModalidadeComId(3L);

        when(modalidadeRepository.findAll()).thenReturn(Arrays.asList(modalidade, modalidade2, modalidade3));

        //when
        var response = modalidadeService.listarModalidades();

        //then
        assertNotNull(response);
        assertEquals(3, response.size());
        assertEquals(1L, response.get(0).id());
        assertEquals(2L, response.get(1).id());
        assertEquals(3L, response.get(2).id());

        verify(modalidadeRepository).findAll();
        verifyNoMoreInteractions(modalidadeRepository);
    }

    @Test
    void deveBuscarTodasModalidadesAtivas() {
        var modalidade = criarModalidadeComIdEStatus(1L, true);
        var modalidade2 = criarModalidadeComIdEStatus(2L, true);


        when(modalidadeRepository.findByAtivaTrue()).thenReturn(Arrays.asList(modalidade, modalidade2));

        //when
        var response = modalidadeService.listarModalidadesAtivas();

        //then
        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).id());
        assertEquals(2L, response.get(1).id());

        assertTrue(response.get(0).ativa());
        assertTrue(response.get(1).ativa());

        verify(modalidadeRepository).findByAtivaTrue();
        verifyNoMoreInteractions(modalidadeRepository);
    }

    @Test
    void deveRetornarListaSemModalidade() {

        //given
        when(modalidadeRepository.findAll()).thenReturn(List.of());

        //when
        var response = modalidadeService.listarModalidades();

        //then
        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(modalidadeRepository).findAll();
        verifyNoMoreInteractions(modalidadeRepository);
    }

    // ==========================
    // ATIVAÇÃO/DESATIVAÇÃO
    // ==========================

    @Test
    void deveAtivarUmModalidade() {

        //given
        var modalidade = criarModalidadeComIdEStatus(1L, false);
        when(modalidadeRepository.findById(1L)).thenReturn(Optional.of(modalidade));

        //when
        modalidadeService.ativarModalidade(1L);

        //then
        assertTrue(modalidade.getAtiva());
        verify(modalidadeRepository).findById(1L);
        verify(modalidadeRepository).save(modalidade);
        verifyNoMoreInteractions(modalidadeRepository);
    }

    @Test
    void deveDesativarUmModalidade() {

        //given
        var modalidade = criarModalidadeComIdEStatus(1L, true);
        when(modalidadeRepository.findById(1L)).thenReturn(Optional.of(modalidade));

        //when
        modalidadeService.inativarModalidade(1L);

        //then
        assertFalse(modalidade.getAtiva());

        verify(modalidadeRepository).findById(1L);
        verify(modalidadeRepository).save(modalidade);
        verifyNoMoreInteractions(modalidadeRepository);
    }


    @Test
    void deveLancarExcecaoQuandoModalidadeJaAtiva() {

        //given
        var modalidade = criarModalidadeComIdEStatus(1L, true);

        when(modalidadeRepository.findById(1L)).thenReturn(Optional.of(modalidade));

        //when
        assertThrows(RecursoJaAtivoException.class, () -> modalidadeService.ativarModalidade(1L));

        //then
        assertTrue(modalidade.getAtiva());

        verify(modalidadeRepository).findById(1L);
        verify(modalidadeRepository, never()).save(modalidade);
        verifyNoMoreInteractions(modalidadeRepository);
    }

    @Test
    void deveLancarExcecaoQuandoModalidadeJaInativa() {

        //given
        var modalidade = criarModalidadeComIdEStatus(1L, false);

        when(modalidadeRepository.findById(1L)).thenReturn(Optional.of(modalidade));

        //when
        assertThrows(RecursoJaInativoException.class, () -> modalidadeService.inativarModalidade(1L));

        //then
        assertFalse(modalidade.getAtiva());

        verify(modalidadeRepository).findById(1L);
        verify(modalidadeRepository, never()).save(modalidade);
        verifyNoMoreInteractions(modalidadeRepository);
    }

    @Test
    void deveLancarExcecaoAoAtivarModalidadeInexistente() {

        //given
        when(modalidadeRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        assertThrows(ModalidadeNaoEncontradaException.class, () -> modalidadeService.ativarModalidade(1L));

        //then
        verify(modalidadeRepository).findById(1L);
        verifyNoMoreInteractions(modalidadeRepository);
    }

    @Test
    void deveLancarExcecaoAoInativarModalidadeInexistente() {
        //given
        when(modalidadeRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        assertThrows(ModalidadeNaoEncontradaException.class, () -> modalidadeService.inativarModalidade(1L));

        //then
        verify(modalidadeRepository).findById(1L);
        verifyNoMoreInteractions(modalidadeRepository);
    }



    // ==========================
    // EXCLUSÃO
    // ==========================

    @Test
    void deveExcluirModalidadeComSucesso() {

        //given
        var modalidade = criarModalidade();
        when(modalidadeRepository.findById(1L)).thenReturn(Optional.of(modalidade));

        //when
        modalidadeService.removerModalidade(1L);

        //then
        verify(modalidadeRepository).findById(1L);
        verify(modalidadeRepository).delete(modalidade);
        verifyNoMoreInteractions(modalidadeRepository);
    }

    @Test
    void deveLancarExcecaoAoExcluir() {

        //given
        when(modalidadeRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        assertThrows(ModalidadeNaoEncontradaException.class, () -> modalidadeService.removerModalidade(1L));

        //then
        verify(modalidadeRepository).findById(1L);
        verify(modalidadeRepository, never()).delete(any(Modalidade.class));
        verifyNoMoreInteractions(modalidadeRepository);
    }

    // ==========================
    // Utils
    // ==========================

    private static @NotNull Modalidade criarModalidade() {
        Modalidade modalidade = ModalidadeTestFactory.modalidadeRequest().toEntity();
        modalidade.setId(1L);
        return modalidade;
    }

    private static @NotNull Modalidade criarModalidadeComId(Long id) {
        Modalidade modalidade = ModalidadeTestFactory.modalidadeRequest().toEntity();
        modalidade.setId(id);
        return modalidade;
    }

    private static @NotNull Modalidade criarModalidadeComIdEStatus(Long id, Boolean status) {
        Modalidade modalidade = ModalidadeTestFactory.modalidadeRequest().toEntity();
        modalidade.setId(id);
        modalidade.setAtiva(status);
        return modalidade;
    }

}

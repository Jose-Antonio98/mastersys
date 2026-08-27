package dev.jose.mastersys.service;

import dev.jose.mastersys.domain.Aluno;
import dev.jose.mastersys.dto.AlunoFiltroRequest;
import dev.jose.mastersys.dto.AlunoResponse;
import dev.jose.mastersys.exception.AlunoNaoEncontradoException;
import dev.jose.mastersys.exception.RecursoJaCadastradoException;
import dev.jose.mastersys.factory.AlunoAtualizacaoRequestBuilder;
import dev.jose.mastersys.factory.AlunoTestFactory;
import dev.jose.mastersys.repository.AlunoRepository;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private AlunoService alunoService;

    // ==========================
    // CADASTRO
    // ==========================

    @Test
    void deveCadastrarAluno(){

        //given
        var request = AlunoTestFactory.alunoRequest();

        when(alunoRepository.save(any(Aluno.class))).thenAnswer(
                invocation -> invocation.getArgument(0));
        when(alunoRepository.existsByEmailIgnoreCase(request.email())).thenReturn(false);

        //when
        var response = alunoService.cadastrar(request);

        //then
        assertNotNull(response);
        assertEquals(request.nome(), response.nome());
        assertEquals(request.dataNascimento(), response.dataNascimento());
        assertEquals(request.sexo(), response.sexo());
        assertEquals(request.celular(), response.celular());
        assertEquals(request.email(), response.email());
        assertEquals(request.cidade(), response.cidade());
        assertEquals(request.estado(), response.estado());
        assertEquals(request.observacao(), response.observacoes());

        verify(alunoRepository).existsByEmailIgnoreCase(request.email());
        verify(alunoRepository).save(any(Aluno.class));
        verifyNoMoreInteractions(alunoRepository);
    }

    @Test
    void deveImpedirCadastroComEmailDuplicado(){

        //given
        var request = AlunoTestFactory.alunoRequest();

        when(alunoRepository.existsByEmailIgnoreCase(request.email())).thenReturn(true);

        //when
        assertThrows(RecursoJaCadastradoException.class, () -> alunoService.cadastrar(request));

        //then
        verify(alunoRepository, never()).save(any(Aluno.class));
        verifyNoMoreInteractions(alunoRepository);
    }

    // ==========================
    // CONSULTA
    // ==========================

    @Test
    void deveBuscarAlunoPorId(){

        //given
        Aluno aluno = criarAluno();

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));

        //when
        var response = alunoService.buscarPorId(1L);

        //then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(aluno.getNome(), response.nome());
        assertEquals(aluno.getDataNascimento(), response.dataNascimento());
        assertEquals(aluno.getSexo(), response.sexo());
        assertEquals(aluno.getCelular(), response.celular());
        assertEquals(aluno.getEmail(), response.email());
        assertEquals(aluno.getCidade(), response.cidade());
        assertEquals(aluno.getEstado(), response.estado());
        assertEquals(aluno.getObservacao(), response.observacoes());

        verify(alunoRepository).findById(1L);
        verifyNoMoreInteractions(alunoRepository);
    }

    @Test
    void deveLancarExcecaoQuandoAlunoNaoExistir() {

        //given
        when(alunoRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        assertThrows(AlunoNaoEncontradoException.class, () -> alunoService.buscarPorId(1L));

        //then
        verify(alunoRepository).findById(1L);
        verifyNoMoreInteractions(alunoRepository);
    }

    // ==========================
    // ATUALIZAÇÃO
    // ==========================

    @Test
    void deveAtualizarAluno() {

        //given
        Aluno aluno = criarAluno();

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        var request = AlunoTestFactory.alunoRequestAtualizar();

        when(alunoRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), 1L)).thenReturn(false);

        //when
        var response = alunoService.atualizar(1L, request);

        //then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(request.nome(), response.nome());
        assertEquals(request.dataNascimento(), response.dataNascimento());
        assertEquals(request.sexo(), response.sexo());
        assertEquals(request.celular(), response.celular());
        assertEquals(request.email(), response.email());
        assertEquals(request.cidade(), response.cidade());
        assertEquals(request.estado(), response.estado());
        assertEquals(request.observacao(), response.observacoes());

        verify(alunoRepository).findById(1L);
        ArgumentCaptor<Aluno> alunoCaptor = ArgumentCaptor.forClass(Aluno.class);
        verify(alunoRepository).save(alunoCaptor.capture());

        var alunoSalvo = alunoCaptor.getValue();
        assertEquals(1L, alunoSalvo.getId());
        assertEquals(request.nome(), alunoSalvo.getNome());
        assertEquals(request.dataNascimento(), alunoSalvo.getDataNascimento());
        assertEquals(request.sexo(), alunoSalvo.getSexo());
        assertEquals(request.celular(), alunoSalvo.getCelular());
        assertEquals(request.email(), alunoSalvo.getEmail());
        assertEquals(request.cidade(), alunoSalvo.getCidade());
        assertEquals(request.estado(), alunoSalvo.getEstado());
        assertEquals(request.observacao(), alunoSalvo.getObservacao());
        verify(alunoRepository).existsByEmailIgnoreCaseAndIdNot(request.email(), alunoSalvo.getId());
        verifyNoMoreInteractions(alunoRepository);
    }


    @Test
    void deveImpedirAtualizacaoComEmailDuplicado() {
        //given
        Aluno aluno = criarAluno();

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));

        var request = AlunoTestFactory.alunoRequestAtualizar();

        when(alunoRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), 1L)).thenReturn(true);

        //when
        assertThrows(RecursoJaCadastradoException.class, () -> alunoService.atualizar(1L, request));

        //then
        verify(alunoRepository).findById(1L);
        verify(alunoRepository,  never()).save(any(Aluno.class));
        verify(alunoRepository).existsByEmailIgnoreCaseAndIdNot(request.email(), 1L);
        verifyNoMoreInteractions(alunoRepository);
    }

    // ==========================
    // ATUALIZAÇÃO PARCIAL
    // ==========================

    @Test
    void deveAtualizarAlunoParcialmente() {

        //given
        Aluno aluno = criarAluno();

        Aluno alunoOriginal = criarAluno();


        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any(Aluno.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var request = new AlunoAtualizacaoRequestBuilder().nome("dante alighieri").build();

        //when
        var response = alunoService.atualizarParcial(1L, request);

        //then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(request.nome(), response.nome());

        verify(alunoRepository).findById(1L);
        ArgumentCaptor<Aluno> alunoCaptor = ArgumentCaptor.forClass(Aluno.class);
        verify(alunoRepository).save(alunoCaptor.capture());

        var alunoSalvo = alunoCaptor.getValue();
        assertEquals(1L, alunoSalvo.getId());
        assertEquals(request.nome(), alunoSalvo.getNome());

        assertEquals(alunoOriginal.getDataNascimento(), alunoSalvo.getDataNascimento());
        assertEquals(alunoOriginal.getSexo(), alunoSalvo.getSexo());
        assertEquals(alunoOriginal.getCelular(), alunoSalvo.getCelular());
        assertEquals(alunoOriginal.getEmail(), alunoSalvo.getEmail());
        assertEquals(alunoOriginal.getCidade(), alunoSalvo.getCidade());
        assertEquals(alunoOriginal.getEstado(), alunoSalvo.getEstado());
        assertEquals(alunoOriginal.getObservacao(), alunoSalvo.getObservacao());


        verify(alunoRepository, never()).existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong());
        verifyNoMoreInteractions(alunoRepository);
    }

    @Test
    void deveAtualizarNomeEmailEDataNascimento(){

        //given
        Aluno aluno = criarAluno();

        Aluno alunoOriginal = criarAluno();

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any(Aluno.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var request = new AlunoAtualizacaoRequestBuilder().nome("dante alighieri").email("dante@gmail.com")
                .dataNascimento(LocalDate.of(1265, 9, 14)).build();

        //when

        var response = alunoService.atualizarParcial(1L, request);

        //then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(request.nome(), response.nome());
        assertEquals(request.dataNascimento(), response.dataNascimento());
        assertEquals(request.email(), response.email());

        verify(alunoRepository).findById(1L);
        ArgumentCaptor<Aluno> alunoCaptor = ArgumentCaptor.forClass(Aluno.class);
        verify(alunoRepository).save(alunoCaptor.capture());

        var alunoSalvo = alunoCaptor.getValue();
        assertEquals(1L, alunoSalvo.getId());
        assertEquals(request.nome(), alunoSalvo.getNome());

        assertEquals(alunoOriginal.getCidade(), alunoSalvo.getCidade());
        assertEquals(alunoOriginal.getEstado(), alunoSalvo.getEstado());
        assertEquals(alunoOriginal.getObservacao(), alunoSalvo.getObservacao());
        assertEquals(alunoOriginal.getCelular(), alunoSalvo.getCelular());
        assertEquals(alunoOriginal.getSexo(), alunoSalvo.getSexo());


        verify(alunoRepository).existsByEmailIgnoreCaseAndIdNot(request.email(), 1L);
        verifyNoMoreInteractions(alunoRepository);

    }

    @Test
    void deveImpedirAtualizacaoParcialComEmailDuplicado(){

        //given
        Aluno aluno = criarAluno();

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.existsByEmailIgnoreCaseAndIdNot(aluno.getEmail(), 1L)).thenReturn(true);

        var request = new AlunoAtualizacaoRequestBuilder().email("jose@email.com").build();

        //when
        assertThrows(RecursoJaCadastradoException.class, () -> alunoService.atualizarParcial(1L, request));

        //then
        verify(alunoRepository).findById(1L);
        verify(alunoRepository, never()).save(any(Aluno.class));
        verify(alunoRepository).existsByEmailIgnoreCaseAndIdNot(aluno.getEmail(), 1L);
        verifyNoMoreInteractions(alunoRepository);
    }


    // ==========================
    // EXCLUSÃO
    // ==========================

    @Test
    void deveExcluirAluno() {

        //given
        var aluno = AlunoTestFactory.alunoRequest().toEntity();
        aluno.setId(1L);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));

        //when
        alunoService.excluir(1L);

        //then
        verify(alunoRepository).findById(1L);
        verify(alunoRepository).delete(aluno);
        verifyNoMoreInteractions(alunoRepository);
    }

    @Test
    void deveImpedirExcluirAlunoInexistente(){

        //given
        when(alunoRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        assertThrows(AlunoNaoEncontradoException.class, () -> alunoService.excluir(1L));

        //then
        verify(alunoRepository).findById(1L);
        verify(alunoRepository, never()).delete(any(Aluno.class));
        verifyNoMoreInteractions(alunoRepository);

    }

    // ==========================
    // BUSCA
    // ==========================

    @Test
    void deveListarAlunosComSucesso(){
        var aluno1 = criarAlunoComId(1L);
        var aluno2 = criarAlunoComId(2L);
        var aluno3 = criarAlunoComId(3L);

        Page<Aluno> pagina = new PageImpl<>(List.of(aluno1, aluno2, aluno3));

        Pageable pageable = PageRequest.of(0, 10);

        when(alunoRepository.findAll(ArgumentMatchers.<Specification<Aluno>>any(), eq(pageable))).thenReturn(pagina);

        var filtro = new AlunoFiltroRequest(null, null, null, null, null);

        //when
        Page<AlunoResponse> resultado = alunoService.listar(filtro, pageable);


        //then
        assertNotNull(resultado);
        assertEquals(3, resultado.getTotalElements());
        assertEquals(3, resultado.getContent().size());

        verify(alunoRepository).findAll(ArgumentMatchers.<Specification<Aluno>>any(), eq(pageable));
        verifyNoMoreInteractions(alunoRepository);
    }

    @Test
    void deveRetornarPaginaVaziaQuandoNaoExistiremAlunos() {

        //given
        Page<Aluno> paginaVazia = Page.empty();

        when(alunoRepository.findAll(ArgumentMatchers.<Specification<Aluno>>any(),
                any(Pageable.class))).thenReturn(paginaVazia);

        var filtro = new AlunoFiltroRequest(null, null, null, null, null);

        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<AlunoResponse> resultado = alunoService.listar(filtro, pageable);

        //then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        assertEquals(0, resultado.getTotalElements());

        verify(alunoRepository).findAll(ArgumentMatchers.<Specification<Aluno>>any(), eq(pageable));
        verifyNoMoreInteractions(alunoRepository);
    }

    @Test
    void deveRespeitarPaginacaoAoListarAlunos() {

        // given
        var aluno1 = criarAlunoComId(3L);
        var aluno2 = criarAlunoComId(4L);

        Page<Aluno> pagina = new PageImpl<>(List.of(aluno1, aluno2),
                PageRequest.of(1, 2), 5);

        Pageable pageable = PageRequest.of(1, 2);

        when(alunoRepository.findAll(ArgumentMatchers.<Specification<Aluno>>any(), eq(pageable))).thenReturn(pagina);

        var filtro = new AlunoFiltroRequest(null, null, null, null, null);

        // when
        Page<AlunoResponse> resultado = alunoService.listar(filtro, pageable);

        // then
        assertNotNull(resultado);

        assertEquals(1, resultado.getNumber());
        assertEquals(2, resultado.getSize());
        assertEquals(2, resultado.getNumberOfElements());
        assertEquals(5, resultado.getTotalElements());
        assertEquals(3, resultado.getTotalPages());

        verify(alunoRepository).findAll(ArgumentMatchers.<Specification<Aluno>>any(), eq(pageable));
        verifyNoMoreInteractions(alunoRepository);
    }

    private static @NonNull Aluno criarAluno() {
        Aluno aluno = AlunoTestFactory.alunoRequest().toEntity();
        aluno.setId(1L);
        return aluno;
    }

    private static @NonNull Aluno criarAlunoComId(Long id) {
        Aluno aluno = AlunoTestFactory.alunoRequest().toEntity();
        aluno.setId(id);
        return aluno;
    }
}

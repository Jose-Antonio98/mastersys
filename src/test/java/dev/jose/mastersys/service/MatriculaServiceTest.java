package dev.jose.mastersys.service;

import dev.jose.mastersys.domain.Aluno;
import dev.jose.mastersys.domain.Matricula;
import dev.jose.mastersys.domain.enums.StatusMatricula;
import dev.jose.mastersys.dto.MatriculaFiltroRequest;
import dev.jose.mastersys.dto.MatriculaResponse;
import dev.jose.mastersys.exception.*;
import dev.jose.mastersys.factory.AlunoTestFactory;
import dev.jose.mastersys.factory.MatriculaTestFactory;
import dev.jose.mastersys.repository.AlunoRepository;
import dev.jose.mastersys.repository.MatriculaRepository;
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
public class MatriculaServiceTest {

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private MatriculaService matriculaService;

    // ==========================
    // CADASTRO
    // ==========================

    @Test
    void deveMatricularComSucesso() {

        //given
        var request = MatriculaTestFactory.matriculaRequest();
        var aluno = criarAluno();

        when(matriculaRepository.existsByAlunoIdAndStatus(request.alunoId(), StatusMatricula.ATIVA))
                .thenReturn(false);

        when(alunoRepository.findById(request.alunoId())).thenReturn(Optional.of(aluno));

        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(
                i -> {
                    Matricula matricula = i.getArgument(0);
                    matricula.setId(1L);
                    return matricula;});

        //when
        var response= matriculaService.criarMatricula(request);


        //then

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(request.diaVencimento(), response.diaVencimento());
        assertEquals(aluno, response.aluno());
        assertEquals(StatusMatricula.ATIVA, response.status());

        verify(matriculaRepository).existsByAlunoIdAndStatus(request.alunoId(), StatusMatricula.ATIVA);

        verify(alunoRepository).findById(request.alunoId());

        ArgumentCaptor<Matricula> captor = ArgumentCaptor.forClass(Matricula.class);

        verify(matriculaRepository).save(captor.capture());

        var matriculaSalva = captor.getValue();

        assertEquals(aluno, matriculaSalva.getAluno());
        assertEquals(request.diaVencimento(), matriculaSalva.getDiaVencimento());

        verifyNoMoreInteractions(matriculaRepository, alunoRepository);
    }

    @Test
    void deveImpedirMatriculaComAlunoNaoEncontrado() {

        //given
        var request = MatriculaTestFactory.matriculaRequest();

        when(matriculaRepository.existsByAlunoIdAndStatus(request.alunoId(), StatusMatricula.ATIVA))
                .thenReturn(false);

        when(alunoRepository.findById(request.alunoId())).thenReturn(Optional.empty());

        //when
        assertThrows(AlunoNaoEncontradoException.class, () -> matriculaService.criarMatricula(request));

        //then
        verify(matriculaRepository).existsByAlunoIdAndStatus(request.alunoId(), StatusMatricula.ATIVA);
        verify(alunoRepository).findById(request.alunoId());
        verify(matriculaRepository, never()).save(any(Matricula.class));
        verifyNoMoreInteractions(matriculaRepository, alunoRepository);

    }

    @Test
    void deveImpedirMatriculaDuplicada() {

        //given
        var request = MatriculaTestFactory.matriculaRequest();

        when(matriculaRepository.existsByAlunoIdAndStatus(request.alunoId(), StatusMatricula.ATIVA))
                .thenReturn(true);

        //when
        assertThrows(RecursoJaCadastradoException.class, () -> matriculaService.criarMatricula(request));

        //then
        verify(matriculaRepository).existsByAlunoIdAndStatus(request.alunoId(), StatusMatricula.ATIVA);
        verify(alunoRepository, never()).findById(request.alunoId());
        verify(matriculaRepository, never()).save(any(Matricula.class));
        verifyNoMoreInteractions(matriculaRepository, alunoRepository);
    }

    // ==========================
    // BUSCA
    // ==========================

    @Test
    void deveBuscarMatriculaPorIdComSucesso() {

        //given
        var request = criarMatricula();

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(request));

        //when
        var response = matriculaService.buscarPorId(request.getId());

        //then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(request.getDiaVencimento(), response.diaVencimento());
        assertEquals(request.getStatus(), response.status());
        assertEquals(request.getAluno(), response.aluno());

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
    }

    @Test
    void deveBuscarMatriculaPorIdNaoEncontrado() {

        //given
        when(matriculaRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        assertThrows(MatriculaNaoEncontradaException.class, () -> matriculaService.buscarPorId(1L));

        //then
        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
    }

    @Test
    void deveBuscarTodasMatriculas() {
        var matricula = criarMatriculaComId(1L);
        var matricula2 = criarMatriculaComId(2L);
        var matricula3 = criarMatriculaComId(3L);

        Page<Matricula> pagina = new PageImpl<>(List.of(matricula, matricula2, matricula3));

        Pageable pageable = PageRequest.of(0, 10);

        when(matriculaRepository.findAll(ArgumentMatchers.<Specification<Matricula>>any(), eq(pageable)))
                .thenReturn(pagina);

        var filtro = new MatriculaFiltroRequest(null, null, null, null,
                null,null, null, null);

        //when
        Page<MatriculaResponse> resultado = matriculaService.listar(filtro, pageable);

        //then
        assertNotNull(resultado);
        assertEquals(3, resultado.getTotalElements());
        assertEquals(3, resultado.getContent().size());

        verify(matriculaRepository).findAll(ArgumentMatchers.<Specification<Matricula>>any(), eq(pageable));
        verifyNoMoreInteractions(matriculaRepository);
    }

    @Test
    void deveRetornarPaginaVazia() {

        //given
        Page<Matricula> paginaVazia = Page.empty();

        when(matriculaRepository.findAll(ArgumentMatchers.<Specification<Matricula>>any(),
                any(Pageable.class))).thenReturn(paginaVazia);

        var filtro = new MatriculaFiltroRequest(null, null, null, null,
                null,null, null, null);

        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<MatriculaResponse> resultado = matriculaService.listar(filtro, pageable);

        //then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        assertEquals(0, resultado.getTotalElements());

        verify(matriculaRepository).findAll(ArgumentMatchers.<Specification<Matricula>>any(), eq(pageable));
        verifyNoMoreInteractions(matriculaRepository);
    }

    // ==========================
    // ATUALIZAÇÃO
    // ==========================

    @Test
    void deveAlterarDiaVencimentoComSucesso() {

        //given
        var matricula = criarMatricula();

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        //when
        matriculaService.alterarDiaVencimento(1L, 20);

        //then
        assertEquals(1L, matricula.getId());
        assertEquals(20, matricula.getDiaVencimento());

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
    }

    @Test
    void deveAlterarDiaVencimentoComDiaNulo() {

        //given
        var matricula = criarMatricula();
        var diaOriginal =  matricula.getDiaVencimento();

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        //when
        assertThrows(DiaVencimentoInvalidoException.class,
                () -> matriculaService.alterarDiaVencimento(1L, null));

        //then
        assertEquals(diaOriginal, matricula.getDiaVencimento());

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
    }

    @Test
    void deveAlterarDiaVencimentoComDiaNegativo() {

        //given
        var matricula = criarMatricula();
        var diaOriginal =  matricula.getDiaVencimento();

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        //when
        assertThrows(DiaVencimentoInvalidoException.class,
                () -> matriculaService.alterarDiaVencimento(1L, -10));

        //then
        assertEquals(diaOriginal, matricula.getDiaVencimento());

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
    }

    @Test
    void deveAlterarDiaVencimentoComDiaMaiorPermitido() {

        //given
        var matricula = criarMatricula();
        var diaOriginal =  matricula.getDiaVencimento();

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        //when
        assertThrows(DiaVencimentoInvalidoException.class,
                () -> matriculaService.alterarDiaVencimento(1L, 99));

        //then
        assertEquals(diaOriginal, matricula.getDiaVencimento());

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
    }

    @Test
    void deveAlterarDiaVencimentoComDiaJaCadastrado() {

        //given
        var matricula = criarMatricula();
        var diaOriginal =  matricula.getDiaVencimento();

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        //when
        assertThrows(DiaVencimentoInvalidoException.class,
                () -> matriculaService.alterarDiaVencimento(1L, 10));

        //then
        assertEquals(diaOriginal, matricula.getDiaVencimento());

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
    }

    @Test
    void deveEncerrarMatriculaComSucesso() {

        //given
        var matricula = criarMatricula();

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        //when
        matriculaService.encerrarMatricula(1L);

        //then
        assertEquals(1L, matricula.getId());
        assertNotNull(matricula.getDataEncerramento());
        assertEquals(StatusMatricula.ENCERRADA, matricula.getStatus());

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
    }

    @Test
    void deveImpedirEncerrarMatriculaJaEncerrada() {

        //given
        var matricula = criarMatricula();
        matricula.setStatus(StatusMatricula.ENCERRADA);
        var dataOriginal = matricula.getDataEncerramento();

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        //when
        assertThrows(StatusMatriculaInvalidoException.class, () -> matriculaService.encerrarMatricula(1L));

        //then
        assertEquals(1L, matricula.getId());
        assertEquals(StatusMatricula.ENCERRADA, matricula.getStatus());
        assertEquals(dataOriginal, matricula.getDataEncerramento());

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
    }

    @Test
    void deveImpedirEncerrarMatriculaCancelada() {

        //given
        var matricula = criarMatricula();
        matricula.setStatus(StatusMatricula.CANCELADA);
        var dataOriginal = matricula.getDataEncerramento();

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        //when
        assertThrows(StatusMatriculaInvalidoException.class, () -> matriculaService.encerrarMatricula(1L));

        //then
        assertEquals(1L, matricula.getId());
        assertEquals(dataOriginal, matricula.getDataEncerramento());
        assertEquals(StatusMatricula.CANCELADA, matricula.getStatus());

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
    }

    @Test
    void deveCancelarMatriculaComSucesso() {

        //given
        var matricula = criarMatricula();

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        //when
        matriculaService.cancelarMatricula(1L);

        //then
        assertEquals(1L, matricula.getId());
        assertNotNull(matricula.getDataEncerramento());
        assertEquals(StatusMatricula.CANCELADA, matricula.getStatus());

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
    }

    @Test
    void deveImpedirCancelarMatriculaJaCancelada() {

        //given
        var matricula = criarMatricula();
        matricula.setStatus(StatusMatricula.CANCELADA);
        var dataOriginal = matricula.getDataEncerramento();

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        //when
        assertThrows(StatusMatriculaInvalidoException.class, () -> matriculaService.cancelarMatricula(1L));

        //then
        assertEquals(1L, matricula.getId());
        assertEquals(dataOriginal, matricula.getDataEncerramento());
        assertEquals(StatusMatricula.CANCELADA, matricula.getStatus());

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
    }

    @Test
    void deveImpedirCancelarMatriculaEncerrada() {

        //given
        var matricula = criarMatricula();
        matricula.setStatus(StatusMatricula.ENCERRADA);
        var dataOriginal = matricula.getDataEncerramento();

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        //when
        assertThrows(StatusMatriculaInvalidoException.class, () -> matriculaService.cancelarMatricula(1L));

        //then
        assertEquals(1L, matricula.getId());
        assertEquals(dataOriginal, matricula.getDataEncerramento());
        assertEquals(StatusMatricula.ENCERRADA, matricula.getStatus());

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
    }

    @Test
    void deveAtivarMatriculaComSucesso() {

        //given
        var matricula = criarMatricula();
        matricula.setStatus(StatusMatricula.ENCERRADA);
        matricula.setDataEncerramento(LocalDate.now());

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        //when
        matriculaService.ativarMatricula(1L);

        //then
        assertEquals(1L, matricula.getId());
        assertNull(matricula.getDataEncerramento());
        assertEquals(StatusMatricula.ATIVA, matricula.getStatus());

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
    }

    @Test
    void deveImpedirAtivarMatriculaJaAtiva() {

        //given
        var matricula = criarMatricula();

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        //when
        assertThrows(StatusMatriculaInvalidoException.class, () -> matriculaService.ativarMatricula(1L));

        //then
        assertEquals(1L, matricula.getId());
        assertNull(matricula.getDataEncerramento());
        assertEquals(StatusMatricula.ATIVA, matricula.getStatus());

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
    }

    @Test
    void deveImpedirAtivarMatriculaCancelada() {

        //given
        var matricula = criarMatricula();
        matricula.setStatus(StatusMatricula.CANCELADA);
        matricula.setDataEncerramento(LocalDate.now());
        var dataOriginal = matricula.getDataEncerramento();

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        //when
        assertThrows(StatusMatriculaInvalidoException.class, () -> matriculaService.ativarMatricula(1L));

        //then
        assertEquals(1L, matricula.getId());
        assertEquals( dataOriginal, matricula.getDataEncerramento());
        assertEquals(StatusMatricula.CANCELADA, matricula.getStatus());

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(matriculaRepository);
    }


    // ==========================
    // PRIVADOS
    // ==========================

    private static @NonNull Matricula criarMatricula() {

        var aluno = criarAluno();
        var matricula = MatriculaTestFactory.matriculaRequest().toEntity(aluno);
        matricula.setId(1L);
        return matricula;
    }

    private static @NonNull Matricula criarMatriculaComId(Long id) {

        var aluno = criarAluno();
        var matricula = MatriculaTestFactory.matriculaRequest().toEntity(aluno);
        matricula.setId(id);
        return matricula;
    }

    private static @NonNull Aluno criarAluno() {
        var aluno = AlunoTestFactory.alunoRequest().toEntity();
        aluno.setId(1L);
        return aluno;
    }
}

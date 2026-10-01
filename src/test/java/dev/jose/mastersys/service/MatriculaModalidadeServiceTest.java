package dev.jose.mastersys.service;

import dev.jose.mastersys.exception.RecursoJaCadastradoException;
import dev.jose.mastersys.exception.RecursoJaInativoException;
import dev.jose.mastersys.factory.ModalidadeTestFactory;
import dev.jose.mastersys.factory.PlanoTestFactory;
import dev.jose.mastersys.matricula.domain.Matricula;
import dev.jose.mastersys.matricula.domain.MatriculaModalidade;
import dev.jose.mastersys.matricula.domain.enums.StatusMatricula;
import dev.jose.mastersys.matricula.dto.MatriculaModalidadeRequest;
import dev.jose.mastersys.matricula.exception.MatriculaModalidadeNaoEncontradaException;
import dev.jose.mastersys.matricula.exception.MatriculaNaoEncontradaException;
import dev.jose.mastersys.matricula.exception.StatusMatriculaInvalidoException;
import dev.jose.mastersys.matricula.repository.MatriculaModalidadeRepository;
import dev.jose.mastersys.matricula.repository.MatriculaRepository;
import dev.jose.mastersys.matricula.service.MatriculaModalidadeService;
import dev.jose.mastersys.modalidade.domain.Modalidade;
import dev.jose.mastersys.plano.domain.Plano;
import dev.jose.mastersys.plano.exception.PlanoNaoEncontradoException;
import dev.jose.mastersys.plano.repository.PlanosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriculaModalidadeServiceTest {

    @Mock
    private PlanosRepository planosRepository;

    @Mock
    private MatriculaModalidadeRepository matriculaModalidadeRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @InjectMocks
    private MatriculaModalidadeService service;

    @Test
    void deveCriarVinculoComSucesso() {
        var request = new MatriculaModalidadeRequest(1L, 2L);
        var matricula = criarMatricula();
        var plano = criarPlano();

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));
        when(planosRepository.findById(2L)).thenReturn(Optional.of(plano));
        when(matriculaModalidadeRepository.existsByMatriculaIdAndModalidadeIdAndDataFimIsNull(1L, 3L))
                .thenReturn(false);
        when(matriculaModalidadeRepository.save(any(MatriculaModalidade.class))).thenAnswer(invocation -> {
            var vinculo = invocation.getArgument(0, MatriculaModalidade.class);
            vinculo.setId(4L);
            return vinculo;
        });

        var response = service.criarMatriculaModalidade(request);

        assertEquals(4L, response.id());
        assertEquals(1L, response.matriculaId());
        assertEquals(3L, response.modalidadeId());
        assertEquals(2L, response.planoId());

        var captor = ArgumentCaptor.forClass(MatriculaModalidade.class);
        verify(matriculaModalidadeRepository).save(captor.capture());
        assertSame(matricula, captor.getValue().getMatricula());
        assertSame(plano, captor.getValue().getPlano());
        assertSame(plano.getModalidade(), captor.getValue().getModalidade());
        verifyNoMoreInteractions(planosRepository, matriculaModalidadeRepository, matriculaRepository);
    }

    @Test
    void deveImpedirCriacaoQuandoMatriculaNaoExiste() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MatriculaNaoEncontradaException.class,
                () -> service.criarMatriculaModalidade(new MatriculaModalidadeRequest(1L, 2L)));

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(planosRepository, matriculaModalidadeRepository, matriculaRepository);
    }

    @Test
    void deveImpedirCriacaoQuandoPlanoNaoExiste() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(criarMatricula()));
        when(planosRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(PlanoNaoEncontradoException.class,
                () -> service.criarMatriculaModalidade(new MatriculaModalidadeRequest(1L, 2L)));

        verify(matriculaRepository).findById(1L);
        verify(planosRepository).findById(2L);
        verifyNoMoreInteractions(planosRepository, matriculaModalidadeRepository, matriculaRepository);
    }

    @Test
    void deveImpedirCriacaoParaMatriculaInativa() {
        var matricula = criarMatricula();
        matricula.setStatus(StatusMatricula.ENCERRADA);
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));
        when(planosRepository.findById(2L)).thenReturn(Optional.of(criarPlano()));

        assertThrows(StatusMatriculaInvalidoException.class,
                () -> service.criarMatriculaModalidade(new MatriculaModalidadeRequest(1L, 2L)));

        verify(matriculaRepository).findById(1L);
        verify(planosRepository).findById(2L);
        verifyNoMoreInteractions(planosRepository, matriculaModalidadeRepository, matriculaRepository);
    }

    @Test
    void deveImpedirCriacaoParaPlanoInativo() {
        var plano = criarPlano();
        plano.setAtivo(false);
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(criarMatricula()));
        when(planosRepository.findById(2L)).thenReturn(Optional.of(plano));

        assertThrows(RecursoJaInativoException.class,
                () -> service.criarMatriculaModalidade(new MatriculaModalidadeRequest(1L, 2L)));

        verify(matriculaRepository).findById(1L);
        verify(planosRepository).findById(2L);
        verifyNoMoreInteractions(planosRepository, matriculaModalidadeRepository, matriculaRepository);
    }

    @Test
    void deveImpedirCriacaoParaModalidadeInativa() {
        var plano = criarPlano();
        plano.getModalidade().setAtiva(false);
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(criarMatricula()));
        when(planosRepository.findById(2L)).thenReturn(Optional.of(plano));

        assertThrows(RecursoJaInativoException.class,
                () -> service.criarMatriculaModalidade(new MatriculaModalidadeRequest(1L, 2L)));

        verify(matriculaRepository).findById(1L);
        verify(planosRepository).findById(2L);
        verifyNoMoreInteractions(planosRepository, matriculaModalidadeRepository, matriculaRepository);
    }

    @Test
    void deveImpedirVinculoDuplicado() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(criarMatricula()));
        when(planosRepository.findById(2L)).thenReturn(Optional.of(criarPlano()));
        when(matriculaModalidadeRepository.existsByMatriculaIdAndModalidadeIdAndDataFimIsNull(1L, 3L))
                .thenReturn(true);

        assertThrows(RecursoJaCadastradoException.class,
                () -> service.criarMatriculaModalidade(new MatriculaModalidadeRequest(1L, 2L)));

        verify(matriculaRepository).findById(1L);
        verify(planosRepository).findById(2L);
        verify(matriculaModalidadeRepository).existsByMatriculaIdAndModalidadeIdAndDataFimIsNull(1L, 3L);
        verifyNoMoreInteractions(planosRepository, matriculaModalidadeRepository, matriculaRepository);
    }

    @Test
    void deveListarVinculosDaMatricula() {
        var matricula = criarMatricula();
        var plano = criarPlano();
        var vinculo = criarVinculo(matricula, plano);
        vinculo.setId(4L);
        when(matriculaRepository.existsById(1L)).thenReturn(true);
        when(matriculaModalidadeRepository.findAllByMatriculaId(1L)).thenReturn(List.of(vinculo));

        var resultado = service.listarPorMatricula(1L);

        assertEquals(1, resultado.size());
        assertEquals(4L, resultado.get(0).id());
        assertEquals(1L, resultado.get(0).matriculaId());
        assertEquals(3L, resultado.get(0).modalidadeId());
        assertEquals(2L, resultado.get(0).planoId());
        verify(matriculaRepository).existsById(1L);
        verify(matriculaModalidadeRepository).findAllByMatriculaId(1L);
        verifyNoMoreInteractions(matriculaRepository, matriculaModalidadeRepository);
    }

    @Test
    void deveImpedirListagemQuandoMatriculaNaoExiste() {
        when(matriculaRepository.existsById(1L)).thenReturn(false);

        assertThrows(MatriculaNaoEncontradaException.class, () -> service.listarPorMatricula(1L));

        verify(matriculaRepository).existsById(1L);
        verifyNoMoreInteractions(matriculaRepository, matriculaModalidadeRepository);
    }

    @Test
    void deveBuscarVinculoPorId() {
        var vinculo = criarVinculo(criarMatricula(), criarPlano());
        vinculo.setId(4L);
        when(matriculaModalidadeRepository.findById(4L)).thenReturn(Optional.of(vinculo));

        var response = service.buscarPorId(4L);

        assertEquals(4L, response.id());
        assertEquals(1L, response.matriculaId());
        assertEquals(3L, response.modalidadeId());
        assertEquals(2L, response.planoId());
        verify(matriculaModalidadeRepository).findById(4L);
        verifyNoMoreInteractions(matriculaModalidadeRepository);
    }

    @Test
    void deveLancarExcecaoAoBuscarVinculoInexistente() {
        when(matriculaModalidadeRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(MatriculaModalidadeNaoEncontradaException.class, () -> service.buscarPorId(4L));

        verify(matriculaModalidadeRepository).findById(4L);
        verifyNoMoreInteractions(matriculaModalidadeRepository);
    }

    @Test
    void deveEncerrarVinculoAtivo() {
        var vinculo = criarVinculo(criarMatricula(), criarPlano());
        when(matriculaModalidadeRepository.findById(4L)).thenReturn(Optional.of(vinculo));

        service.encerrarMatriculaModalidade(4L);

        assertEquals(LocalDate.now(), vinculo.getDataFim());
        verify(matriculaModalidadeRepository).findById(4L);
        verifyNoMoreInteractions(matriculaModalidadeRepository);
    }

    @Test
    void deveImpedirEncerrarVinculoJaEncerrado() {
        var vinculo = criarVinculo(criarMatricula(), criarPlano());
        var dataFim = LocalDate.of(2025, 1, 15);
        vinculo.setDataFim(dataFim);
        when(matriculaModalidadeRepository.findById(4L)).thenReturn(Optional.of(vinculo));

        assertThrows(RecursoJaCadastradoException.class, () -> service.encerrarMatriculaModalidade(4L));

        assertEquals(dataFim, vinculo.getDataFim());
        verify(matriculaModalidadeRepository).findById(4L);
        verifyNoMoreInteractions(matriculaModalidadeRepository);
    }

    private Matricula criarMatricula() {
        var matricula = new Matricula();
        matricula.setId(1L);
        matricula.setStatus(StatusMatricula.ATIVA);
        return matricula;
    }

    private Plano criarPlano() {
        var modalidade = ModalidadeTestFactory.modalidadeRequest().toEntity();
        modalidade.setId(3L);
        var plano = PlanoTestFactory.planoRequest().toEntity();
        plano.setId(2L);
        plano.setModalidade(modalidade);
        return plano;
    }

    private MatriculaModalidade criarVinculo(Matricula matricula, Plano plano) {
        var vinculo = new MatriculaModalidade();
        vinculo.setMatricula(matricula);
        vinculo.setModalidade(plano.getModalidade());
        vinculo.setPlano(plano);
        return vinculo;
    }
}

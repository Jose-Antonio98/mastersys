package dev.jose.mastersys.controller;

import dev.jose.mastersys.factory.AlunoTestFactory;
import dev.jose.mastersys.factory.MatriculaTestFactory;
import dev.jose.mastersys.matricula.controller.MatriculaController;
import dev.jose.mastersys.matricula.domain.Matricula;
import dev.jose.mastersys.matricula.domain.enums.StatusMatricula;
import dev.jose.mastersys.matricula.dto.MatriculaFiltroRequest;
import dev.jose.mastersys.matricula.dto.MatriculaResponse;
import dev.jose.mastersys.matricula.service.MatriculaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriculaControllerTest {

    @Mock
    private MatriculaService matriculaService;

    @InjectMocks
    private MatriculaController controller;

    @Test
    void deveDelegarOperacoesDeCriacaoConsultaEListagem() {
        var request = MatriculaTestFactory.matriculaRequest();
        var filtro = new MatriculaFiltroRequest(null, null, null, null, null, null, null, null);
        var pageable = PageRequest.of(0, 10);
        var response = response();
        var pagina = Page.<MatriculaResponse>empty(pageable);
        when(matriculaService.criarMatricula(request)).thenReturn(response);
        when(matriculaService.buscarPorId(1L)).thenReturn(response);
        when(matriculaService.listar(filtro, pageable)).thenReturn(pagina);

        assertSame(response, controller.matricular(request));
        assertSame(response, controller.buscarMatriculaPorId(1L));
        assertSame(pagina, controller.listarMatriculas(filtro, pageable));

        verify(matriculaService).criarMatricula(request);
        verify(matriculaService).buscarPorId(1L);
        verify(matriculaService).listar(filtro, pageable);
        verifyNoMoreInteractions(matriculaService);
    }

    @Test
    void deveDelegarAtualizacaoDeStatusEMesDeVencimento() {
        controller.atualizarDiaVencimento(1L, 20);
        controller.cancelarMatricula(1L);
        controller.encerrarMatricula(1L);
        controller.ativarMatricula(1L);

        verify(matriculaService).alterarDiaVencimento(1L, 20);
        verify(matriculaService).cancelarMatricula(1L);
        verify(matriculaService).encerrarMatricula(1L);
        verify(matriculaService).ativarMatricula(1L);
        verifyNoMoreInteractions(matriculaService);
    }

    private MatriculaResponse response() {
        var aluno = AlunoTestFactory.alunoRequest().toEntity();
        aluno.setId(2L);
        var matricula = new Matricula();
        matricula.setId(1L);
        matricula.setAluno(aluno);
        matricula.setDiaVencimento(10);
        matricula.setDataMatricula(LocalDate.now());
        matricula.setStatus(StatusMatricula.ATIVA);
        return MatriculaResponse.fromEntity(matricula);
    }
}

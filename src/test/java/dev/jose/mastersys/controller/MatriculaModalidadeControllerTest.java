package dev.jose.mastersys.controller;

import dev.jose.mastersys.matricula.controller.MatriculaModalidadeController;
import dev.jose.mastersys.matricula.dto.MatriculaModalidadeRequest;
import dev.jose.mastersys.matricula.dto.MatriculaModalidadeResponse;
import dev.jose.mastersys.matricula.service.MatriculaModalidadeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriculaModalidadeControllerTest {

    @Mock
    private MatriculaModalidadeService service;

    @InjectMocks
    private MatriculaModalidadeController controller;

    @Test
    void deveDelegarOperacoesDeVinculoDeModalidade() {
        var request = new MatriculaModalidadeRequest(1L, 2L);
        var response = new MatriculaModalidadeResponse(3L, null, null, 1L, 4L, 2L);
        var lista = List.of(response);
        when(service.criarMatriculaModalidade(request)).thenReturn(response);
        when(service.listarPorMatricula(1L)).thenReturn(lista);
        when(service.buscarPorId(3L)).thenReturn(response);

        assertSame(response, controller.criarMatriculaModalidade(request));
        assertSame(lista, controller.listarPorMatricula(1L));
        assertSame(response, controller.buscarPorId(3L));
        controller.encerrarMatriculaModalidade(3L);

        verify(service).criarMatriculaModalidade(request);
        verify(service).listarPorMatricula(1L);
        verify(service).buscarPorId(3L);
        verify(service).encerrarMatriculaModalidade(3L);
        verifyNoMoreInteractions(service);
    }
}

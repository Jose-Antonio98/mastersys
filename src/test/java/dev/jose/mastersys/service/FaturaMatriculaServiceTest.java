package dev.jose.mastersys.service;

import dev.jose.mastersys.fatura.domain.FaturaMatricula;
import dev.jose.mastersys.fatura.domain.enums.StatusFatura;
import dev.jose.mastersys.fatura.dto.FaturaFiltroRequest;
import dev.jose.mastersys.fatura.dto.FaturaMatriculaRequest;
import dev.jose.mastersys.fatura.exception.FaturaDuplicadaException;
import dev.jose.mastersys.fatura.exception.FaturaNaoEncontradaException;
import dev.jose.mastersys.fatura.exception.FaturaSemModalidadeAtivaException;
import dev.jose.mastersys.fatura.exception.StatusFaturaInvalidoException;
import dev.jose.mastersys.fatura.repository.FaturaMatriculaRepository;
import dev.jose.mastersys.fatura.service.FaturaMatriculaService;
import dev.jose.mastersys.factory.AlunoTestFactory;
import dev.jose.mastersys.factory.ModalidadeTestFactory;
import dev.jose.mastersys.factory.PlanoTestFactory;
import dev.jose.mastersys.matricula.domain.Matricula;
import dev.jose.mastersys.matricula.domain.MatriculaModalidade;
import dev.jose.mastersys.matricula.domain.enums.StatusMatricula;
import dev.jose.mastersys.matricula.exception.DiaVencimentoInvalidoException;
import dev.jose.mastersys.matricula.exception.MatriculaNaoEncontradaException;
import dev.jose.mastersys.matricula.exception.StatusMatriculaInvalidoException;
import dev.jose.mastersys.matricula.repository.MatriculaModalidadeRepository;
import dev.jose.mastersys.matricula.repository.MatriculaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FaturaMatriculaServiceTest {

    @Mock
    private FaturaMatriculaRepository faturaRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private MatriculaModalidadeRepository matriculaModalidadeRepository;

    @InjectMocks
    private FaturaMatriculaService service;

    @Test
    void deveCriarFaturaComValorDosPlanosAtivos() {
        var matricula = criarMatricula(10);
        var vinculo = criarVinculo(new BigDecimal("130.00"), null);
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));
        when(faturaRepository.existsByMatriculaIdAndDataVencimento(1L, vencimentoNoMes(10))).thenReturn(false);
        when(matriculaModalidadeRepository.findAllByMatriculaId(1L)).thenReturn(List.of(vinculo));
        when(faturaRepository.saveAndFlush(any(FaturaMatricula.class))).thenAnswer(invocation -> {
            var fatura = invocation.getArgument(0, FaturaMatricula.class);
            fatura.setId(5L);
            return fatura;
        });

        var response = service.criarFatura(new FaturaMatriculaRequest(1L));

        assertEquals(5L, response.id());
        assertEquals(vencimentoNoMes(10), response.dataVencimento());
        assertEquals(new BigDecimal("130.00"), response.valor());
        assertEquals(StatusFatura.ABERTA, response.status());
        assertEquals(1L, response.matriculaId());

        var captor = ArgumentCaptor.forClass(FaturaMatricula.class);
        verify(faturaRepository).saveAndFlush(captor.capture());
        assertEquals(new BigDecimal("130.00"), captor.getValue().getValor());
        assertEquals(vencimentoNoMes(10), captor.getValue().getDataVencimento());
        verifyNoMoreInteractions(faturaRepository, matriculaRepository, matriculaModalidadeRepository);
    }

    @Test
    void deveAjustarVencimentoParaUltimoDiaDoMes() {
        var matricula = criarMatricula(31);
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));
        when(faturaRepository.existsByMatriculaIdAndDataVencimento(1L, vencimentoNoMes(31))).thenReturn(false);
        when(matriculaModalidadeRepository.findAllByMatriculaId(1L))
                .thenReturn(List.of(criarVinculo(new BigDecimal("130.00"), null)));
        when(faturaRepository.saveAndFlush(any(FaturaMatricula.class))).thenAnswer(invocation -> {
            var fatura = invocation.getArgument(0, FaturaMatricula.class);
            fatura.setId(5L);
            return fatura;
        });

        var response = service.criarFatura(new FaturaMatriculaRequest(1L));

        assertEquals(YearMonth.now().atEndOfMonth(), response.dataVencimento());
    }

    @Test
    void deveImpedirCriacaoDeFaturaParaMatriculaInexistente() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MatriculaNaoEncontradaException.class,
                () -> service.criarFatura(new FaturaMatriculaRequest(1L)));

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(faturaRepository, matriculaRepository, matriculaModalidadeRepository);
    }

    @Test
    void deveImpedirCriacaoDeFaturaDuplicada() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(criarMatricula(10)));
        when(faturaRepository.existsByMatriculaIdAndDataVencimento(1L, vencimentoNoMes(10))).thenReturn(true);

        assertThrows(FaturaDuplicadaException.class,
                () -> service.criarFatura(new FaturaMatriculaRequest(1L)));

        verify(matriculaRepository).findById(1L);
        verify(faturaRepository).existsByMatriculaIdAndDataVencimento(1L, vencimentoNoMes(10));
        verifyNoMoreInteractions(faturaRepository, matriculaRepository, matriculaModalidadeRepository);
    }

    @Test
    void deveImpedirCriacaoDeFaturaParaMatriculaInativa() {
        var matricula = criarMatricula(10);
        matricula.setStatus(StatusMatricula.ENCERRADA);
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));
        when(faturaRepository.existsByMatriculaIdAndDataVencimento(1L, vencimentoNoMes(10))).thenReturn(false);

        assertThrows(StatusMatriculaInvalidoException.class,
                () -> service.criarFatura(new FaturaMatriculaRequest(1L)));

        verify(matriculaRepository).findById(1L);
        verify(faturaRepository).existsByMatriculaIdAndDataVencimento(1L, vencimentoNoMes(10));
        verifyNoMoreInteractions(faturaRepository, matriculaRepository, matriculaModalidadeRepository);
    }

    @Test
    void deveImpedirCriacaoDeFaturaSemModalidadeAtiva() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(criarMatricula(10)));
        when(faturaRepository.existsByMatriculaIdAndDataVencimento(1L, vencimentoNoMes(10))).thenReturn(false);
        when(matriculaModalidadeRepository.findAllByMatriculaId(1L)).thenReturn(List.of());

        assertThrows(FaturaSemModalidadeAtivaException.class,
                () -> service.criarFatura(new FaturaMatriculaRequest(1L)));

        verify(matriculaRepository).findById(1L);
        verify(faturaRepository).existsByMatriculaIdAndDataVencimento(1L, vencimentoNoMes(10));
        verify(matriculaModalidadeRepository).findAllByMatriculaId(1L);
        verifyNoMoreInteractions(faturaRepository, matriculaRepository, matriculaModalidadeRepository);
    }

    @Test
    void deveIgnorarVinculosDeModalidadeEncerradosAoCalcularValor() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(criarMatricula(10)));
        when(faturaRepository.existsByMatriculaIdAndDataVencimento(1L, vencimentoNoMes(10))).thenReturn(false);
        when(matriculaModalidadeRepository.findAllByMatriculaId(1L)).thenReturn(List.of(
                criarVinculo(new BigDecimal("130.00"), LocalDate.now().minusDays(1)),
                criarVinculo(new BigDecimal("80.00"), null)));
        when(faturaRepository.saveAndFlush(any(FaturaMatricula.class))).thenAnswer(invocation -> {
            var fatura = invocation.getArgument(0, FaturaMatricula.class);
            fatura.setId(5L);
            return fatura;
        });

        var response = service.criarFatura(new FaturaMatriculaRequest(1L));

        assertEquals(new BigDecimal("80.00"), response.valor());
    }

    @Test
    void deveTraduzirViolacaoDeIntegridadeAoSalvarFaturaDuplicada() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(criarMatricula(10)));
        when(faturaRepository.existsByMatriculaIdAndDataVencimento(1L, vencimentoNoMes(10))).thenReturn(false);
        when(matriculaModalidadeRepository.findAllByMatriculaId(1L))
                .thenReturn(List.of(criarVinculo(new BigDecimal("130.00"), null)));
        when(faturaRepository.saveAndFlush(any(FaturaMatricula.class)))
                .thenThrow(new DataIntegrityViolationException("duplicated unique key"));

        assertThrows(FaturaDuplicadaException.class,
                () -> service.criarFatura(new FaturaMatriculaRequest(1L)));

        verify(faturaRepository).saveAndFlush(any(FaturaMatricula.class));
    }

    @Test
    void deveListarFaturasComPaginacao() {
        var matricula = criarMatricula(10);
        var fatura = criarFatura(matricula);
        fatura.setId(5L);
        var pageable = PageRequest.of(0, 10);
        Page<FaturaMatricula> pagina = new PageImpl<>(List.of(fatura), pageable, 1);
        when(faturaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pagina);

        var response = service.listarFaturas(new FaturaFiltroRequest(null, null, null, null), pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(5L, response.getContent().get(0).id());
        assertEquals(1L, response.getContent().get(0).matriculaId());
        verify(faturaRepository).findAll(any(Specification.class), eq(pageable));
        verifyNoMoreInteractions(faturaRepository);
    }

    @Test
    void deveBuscarFaturaPorId() {
        var fatura = criarFatura(criarMatricula(10));
        fatura.setId(5L);
        when(faturaRepository.findById(5L)).thenReturn(Optional.of(fatura));

        var response = service.buscarFaturaPorId(5L);

        assertEquals(5L, response.id());
        assertEquals(fatura.getValor(), response.valor());
        assertEquals(1L, response.matriculaId());
        verify(faturaRepository).findById(5L);
        verifyNoMoreInteractions(faturaRepository);
    }

    @Test
    void deveLancarExcecaoAoBuscarFaturaInexistente() {
        when(faturaRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(FaturaNaoEncontradaException.class, () -> service.buscarFaturaPorId(5L));

        verify(faturaRepository).findById(5L);
        verifyNoMoreInteractions(faturaRepository);
    }

    @Test
    void devePagarFaturaAberta() {
        var fatura = criarFatura(criarMatricula(10));
        when(faturaRepository.findById(5L)).thenReturn(Optional.of(fatura));

        var response = service.pagarFatura(5L);

        assertEquals(StatusFatura.PAGA, response.status());
        assertNotNull(response.dataPagamento());
        verify(faturaRepository).findById(5L);
        verifyNoMoreInteractions(faturaRepository);
    }

    @Test
    void deveImpedirPagamentoDeFaturaCancelada() {
        var fatura = criarFatura(criarMatricula(10));
        fatura.cancelar();
        when(faturaRepository.findById(5L)).thenReturn(Optional.of(fatura));

        assertThrows(StatusFaturaInvalidoException.class, () -> service.pagarFatura(5L));

        assertEquals(StatusFatura.CANCELADA, fatura.getStatusFatura());
        verify(faturaRepository).findById(5L);
        verifyNoMoreInteractions(faturaRepository);
    }

    @Test
    void deveCancelarFaturaAberta() {
        var fatura = criarFatura(criarMatricula(10));
        when(faturaRepository.findById(5L)).thenReturn(Optional.of(fatura));

        service.cancelarFatura(5L);

        assertEquals(StatusFatura.CANCELADA, fatura.getStatusFatura());
        assertEquals(LocalDate.now(), fatura.getDataCancelamento());
        verify(faturaRepository).findById(5L);
        verifyNoMoreInteractions(faturaRepository);
    }

    @Test
    void deveImpedirCancelamentoDeFaturaPaga() {
        var fatura = criarFatura(criarMatricula(10));
        fatura.pagar();
        when(faturaRepository.findById(5L)).thenReturn(Optional.of(fatura));

        assertThrows(StatusFaturaInvalidoException.class, () -> service.cancelarFatura(5L));

        assertEquals(StatusFatura.PAGA, fatura.getStatusFatura());
        verify(faturaRepository).findById(5L);
        verifyNoMoreInteractions(faturaRepository);
    }

    @Test
    void deveGerarFaturasParaMatriculasAtivasSemDuplicarAsExistentes() {
        var matriculaNova = criarMatricula(10);
        matriculaNova.setId(1L);
        var matriculaExistente = criarMatricula(20);
        matriculaExistente.setId(2L);
        when(matriculaRepository.findAllByStatus(StatusMatricula.ATIVA))
                .thenReturn(List.of(matriculaNova, matriculaExistente));
        when(faturaRepository.existsByMatriculaIdAndDataVencimento(1L, vencimentoNoMes(10))).thenReturn(false);
        when(faturaRepository.existsByMatriculaIdAndDataVencimento(2L, vencimentoNoMes(20))).thenReturn(true);
        when(matriculaModalidadeRepository.findAllByMatriculaId(1L))
                .thenReturn(List.of(criarVinculo(new BigDecimal("130.00"), null)));
        when(faturaRepository.saveAndFlush(any(FaturaMatricula.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.gerarFaturasDoPeriodo();

        verify(matriculaRepository).findAllByStatus(StatusMatricula.ATIVA);
        verify(faturaRepository).existsByMatriculaIdAndDataVencimento(1L, vencimentoNoMes(10));
        verify(faturaRepository).existsByMatriculaIdAndDataVencimento(2L, vencimentoNoMes(20));
        verify(faturaRepository).saveAndFlush(any(FaturaMatricula.class));
        verify(matriculaModalidadeRepository).findAllByMatriculaId(1L);
        verifyNoMoreInteractions(faturaRepository, matriculaRepository, matriculaModalidadeRepository);
    }

    @Test
    void deveLancarExcecaoAoCriarFaturaComDiaDeVencimentoInvalido() {
        var matricula = criarMatricula(0);
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        assertThrows(DiaVencimentoInvalidoException.class,
                () -> service.criarFatura(new FaturaMatriculaRequest(1L)));

        verify(matriculaRepository).findById(1L);
        verifyNoMoreInteractions(faturaRepository, matriculaRepository, matriculaModalidadeRepository);
    }

    private Matricula criarMatricula(int diaVencimento) {
        var aluno = AlunoTestFactory.alunoRequest().toEntity();
        aluno.setId(7L);
        var matricula = new Matricula();
        matricula.setId(1L);
        matricula.setAluno(aluno);
        matricula.setDiaVencimento(diaVencimento);
        matricula.setStatus(StatusMatricula.ATIVA);
        return matricula;
    }

    private MatriculaModalidade criarVinculo(BigDecimal valor, LocalDate dataFim) {
        var modalidade = ModalidadeTestFactory.modalidadeRequest().toEntity();
        modalidade.setId(3L);
        var plano = PlanoTestFactory.planoRequest().toEntity();
        plano.setId(2L);
        plano.setValor(valor);
        plano.setModalidade(modalidade);
        var vinculo = new MatriculaModalidade();
        vinculo.setPlano(plano);
        vinculo.setDataFim(dataFim);
        return vinculo;
    }

    private FaturaMatricula criarFatura(Matricula matricula) {
        var fatura = new FaturaMatricula();
        fatura.setId(5L);
        fatura.setMatricula(matricula);
        fatura.setDataVencimento(vencimentoNoMes(matricula.getDiaVencimento()));
        fatura.setValor(new BigDecimal("130.00"));
        return fatura;
    }

    private LocalDate vencimentoNoMes(int dia) {
        return YearMonth.now().atDay(Math.min(dia, YearMonth.now().lengthOfMonth()));
    }
}

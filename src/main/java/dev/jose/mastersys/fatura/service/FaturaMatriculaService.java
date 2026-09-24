package dev.jose.mastersys.fatura.service;

import dev.jose.mastersys.fatura.domain.FaturaMatricula;
import dev.jose.mastersys.fatura.dto.FaturaFiltroRequest;
import dev.jose.mastersys.fatura.dto.FaturaMatriculaRequest;
import dev.jose.mastersys.fatura.dto.FaturaMatriculaResponse;
import dev.jose.mastersys.fatura.exception.FaturaDuplicadaException;
import dev.jose.mastersys.fatura.exception.FaturaNaoEncontradaException;
import dev.jose.mastersys.fatura.exception.FaturaSemModalidadeAtivaException;
import dev.jose.mastersys.fatura.repository.FaturaMatriculaRepository;
import dev.jose.mastersys.fatura.specification.FaturaMatriculaSpecification;
import dev.jose.mastersys.matricula.domain.Matricula;
import dev.jose.mastersys.matricula.domain.enums.StatusMatricula;

import dev.jose.mastersys.matricula.exception.DiaVencimentoInvalidoException;
import dev.jose.mastersys.matricula.exception.MatriculaNaoEncontradaException;
import dev.jose.mastersys.matricula.exception.StatusMatriculaInvalidoException;
import dev.jose.mastersys.matricula.repository.MatriculaModalidadeRepository;
import dev.jose.mastersys.matricula.repository.MatriculaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;


@Service
@Transactional(readOnly = true)
public class FaturaMatriculaService {

    private final FaturaMatriculaRepository faturaMatriculaRepository;
    private final MatriculaRepository matriculaRepository;
    private final MatriculaModalidadeRepository matriculaModalidadeRepository;


    public FaturaMatriculaService(FaturaMatriculaRepository faturaMatriculaRepository,
                                  MatriculaRepository matriculaRepository,
                                  MatriculaModalidadeRepository matriculaModalidadeRepository) {
        this.faturaMatriculaRepository = faturaMatriculaRepository;
        this.matriculaRepository = matriculaRepository;
        this.matriculaModalidadeRepository = matriculaModalidadeRepository;
    }

    @Transactional
    public FaturaMatriculaResponse criarFatura(FaturaMatriculaRequest request) {
        var matricula = buscarMatricula(request.matriculaId());
        var dataVencimento = calcularDataVencimento(matricula.getDiaVencimento());
        validarFaturaNaoDuplicada(matricula, dataVencimento);

        return FaturaMatriculaResponse.fromEntity(
                salvarFatura(matricula, dataVencimento)
        );
    }

    public Page<FaturaMatriculaResponse> listarFaturas(FaturaFiltroRequest filtro, Pageable pageable) {
        return faturaMatriculaRepository.findAll(FaturaMatriculaSpecification.filtros(filtro), pageable)
                .map(FaturaMatriculaResponse::fromEntity);
    }

    public FaturaMatriculaResponse buscarFaturaPorId(Long id) {
        return FaturaMatriculaResponse.fromEntity(buscarFatura(id));
    }

    @Transactional
    public FaturaMatriculaResponse pagarFatura(Long id) {
        var fatura = buscarFatura(id);
        fatura.pagar();

        return FaturaMatriculaResponse.fromEntity(fatura);
    }

    @Transactional
    public void cancelarFatura(Long id) {
        var fatura = buscarFatura(id);
        fatura.cancelar();
    }

    @Transactional
    public void gerarFaturasDoPeriodo() {
        var matriculas = matriculaRepository.findAllByStatus(StatusMatricula.ATIVA);

        for (var matricula : matriculas) {
            var dataVencimento = calcularDataVencimento(matricula.getDiaVencimento());

            if (!faturaMatriculaRepository.existsByMatriculaIdAndDataVencimento(matricula.getId(), dataVencimento)) {
                criarFaturasParaMatricula(matricula, dataVencimento);
            }
        }
    }

    private void criarFaturasParaMatricula(Matricula matricula, LocalDate dataVencimento) {
        salvarFatura(matricula, dataVencimento);
    }

    private Matricula buscarMatricula(Long matriculaId) {
        return matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new MatriculaNaoEncontradaException(matriculaId));
    }

    private BigDecimal buscarValor(Matricula matricula) {
        if (matricula.getStatus() != StatusMatricula.ATIVA) {
            throw new StatusMatriculaInvalidoException("Não é possível gerar fatura para uma matricula inativa");
        }

        var modalidadesAtivas = matriculaModalidadeRepository.findAllByMatriculaId(matricula.getId())
                .stream()
                .filter(vinculo -> vinculo.getDataFim() == null)
                .toList();

        if (modalidadesAtivas.isEmpty()) {
            throw new FaturaSemModalidadeAtivaException(matricula.getId());
        }

        return modalidadesAtivas.stream()
                .map(vinculo -> vinculo.getPlano().getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private FaturaMatricula salvarFatura(Matricula matricula, LocalDate dataVencimento) {
        var fatura = new FaturaMatricula();
        fatura.setMatricula(matricula);
        fatura.setValor(buscarValor(matricula));
        fatura.setDataVencimento(dataVencimento);

        try {
            return faturaMatriculaRepository.saveAndFlush(fatura);
        } catch (DataIntegrityViolationException ex) {
            throw new FaturaDuplicadaException(matricula.getId(), dataVencimento);
        }
    }

    private void validarFaturaNaoDuplicada(Matricula matricula, LocalDate dataVencimento) {
        if (faturaMatriculaRepository.existsByMatriculaIdAndDataVencimento(
                matricula.getId(), dataVencimento)) {
            throw new FaturaDuplicadaException(matricula.getId(), dataVencimento);
        }
    }

    private LocalDate calcularDataVencimento(Integer dia) {
        YearMonth mesAtual = YearMonth.now();

        if (dia == null || dia < 1 || dia > 31) {
            throw new DiaVencimentoInvalidoException("O dia de vencimento deve ser entre 1 e 31");
        }

        YearMonth mesCobranca = dia < LocalDate.now().getDayOfMonth() ? mesAtual.plusMonths(1) : mesAtual;
        int diaValido = Math.min(dia, mesCobranca.lengthOfMonth());

        return mesCobranca.atDay(diaValido);
    }

    private FaturaMatricula buscarFatura(Long id) {
        return faturaMatriculaRepository.findById(id).orElseThrow(() -> new FaturaNaoEncontradaException(id));
    }
}

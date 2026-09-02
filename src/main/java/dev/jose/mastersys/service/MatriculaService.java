package dev.jose.mastersys.service;

import dev.jose.mastersys.domain.Aluno;
import dev.jose.mastersys.domain.Matricula;
import dev.jose.mastersys.domain.enums.StatusMatricula;
import dev.jose.mastersys.dto.MatriculaFiltroRequest;
import dev.jose.mastersys.dto.MatriculaRequest;
import dev.jose.mastersys.dto.MatriculaResponse;
import dev.jose.mastersys.exception.*;
import dev.jose.mastersys.repository.AlunoRepository;
import dev.jose.mastersys.repository.MatriculaRepository;
import dev.jose.mastersys.specification.MatriculaSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;

    public MatriculaService(MatriculaRepository matriculaRepository,  AlunoRepository alunoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.alunoRepository = alunoRepository;
    }

    @Transactional
    public MatriculaResponse criarMatricula(MatriculaRequest request) {
        validarMatriculaDuplicada(request);

        var aluno = validarAluno(request.alunoId());

        return MatriculaResponse.fromEntity(matriculaRepository.save(request.toEntity(aluno)));
    }

    public Page<MatriculaResponse> listar(MatriculaFiltroRequest filtro, Pageable pageable) {
        return matriculaRepository.findAll(MatriculaSpecification.filtros(filtro), pageable)
                .map(MatriculaResponse::fromEntity);
    }

    public MatriculaResponse buscarPorId(Long id){
        return  MatriculaResponse.fromEntity(buscarEntityPorId(id));
    }

    @Transactional
    public void alterarDiaVencimento(Long id, Integer diaVencimento) {
        var matricula = buscarEntityPorId(id);

        validarDiaVencimento(diaVencimento, matricula);

        matricula.setDiaVencimento(diaVencimento);
    }

    @Transactional
    public void encerrarMatricula(Long id){
        var matricula = buscarEntityPorId(id);

        validarEncerrarMatricula(matricula);

        alterarStatus(matricula, StatusMatricula.ENCERRADA);
    }

    @Transactional
    public void cancelarMatricula(Long id){
        var matricula = buscarEntityPorId(id);

        validarCancelamento(matricula);

        alterarStatus(matricula, StatusMatricula.CANCELADA);
    }

    @Transactional
    public void ativarMatricula(Long id){
        var matricula = buscarEntityPorId(id);

        validarAtivacao(matricula);

        alterarStatus(matricula, StatusMatricula.ATIVA);
    }



    //Metodos privados
    private Matricula buscarEntityPorId(Long id){
        return matriculaRepository.findById(id).orElseThrow(() -> new MatriculaNaoEncontradaException(id));
    }

    private void validarMatriculaDuplicada(MatriculaRequest matriculaRequest) {
        if(matriculaRepository.existsByAlunoIdAndStatus(matriculaRequest.alunoId(), StatusMatricula.ATIVA)){
            throw new RecursoJaCadastradoException("Matricula", "O aluno já possui uma matricula ativa.");
        }
    }

    private Aluno validarAluno(Long id) {
        return alunoRepository.findById(id).orElseThrow(() -> new AlunoNaoEncontradoException(id));
    }

    private void alterarStatus(Matricula matricula, StatusMatricula status) {

        matricula.setStatus(status);

        if (status.equals(StatusMatricula.ENCERRADA) || status.equals(StatusMatricula.CANCELADA)) {
            matricula.setDataEncerramento(LocalDate.now());
        }

        if (status.equals(StatusMatricula.ATIVA)) {
            matricula.setDataEncerramento(null);
        }
    }

    private void validarDiaVencimento(Integer diaVencimento, Matricula matricula) {
        if(diaVencimento == null || diaVencimento < 1 || diaVencimento > 31){
            throw new DiaVencimentoInvalidoException("O dia de vencimento deve estar entre 1 e 31.");
        }
        if (Objects.equals(diaVencimento, matricula.getDiaVencimento())) {
            throw new DiaVencimentoInvalidoException("O novo dia de vencimento deve ser diferente do atual.");
        }
    }

    private void validarEncerrarMatricula(Matricula matricula) {
        if (matricula.getStatus() == StatusMatricula.ENCERRADA) {
            throw new StatusMatriculaInvalidoException("A matrícula já está encerrada.");
        }

        if(matricula.getStatus() == StatusMatricula.CANCELADA){
            throw new StatusMatriculaInvalidoException("Uma matrícula cancelada não pode ser encerrada.");
        }
    }

    private void validarCancelamento(Matricula matricula) {
        if (matricula.getStatus() == StatusMatricula.CANCELADA) {
            throw new StatusMatriculaInvalidoException("A matrícula já está cancelada.");
        }

        if(matricula.getStatus() == StatusMatricula.ENCERRADA){
            throw new StatusMatriculaInvalidoException("Uma matrícula encerrada não pode ser cancelada.");
        }
    }

    private void validarAtivacao(Matricula matricula) {
        if(matricula.getStatus() == StatusMatricula.ATIVA){
            throw new StatusMatriculaInvalidoException("A matricula já está ativa.");
        }

        if (matricula.getStatus() == StatusMatricula.CANCELADA) {
            throw new StatusMatriculaInvalidoException( "Uma matrícula cancelada não pode ser reativada.");
        }
    }

}

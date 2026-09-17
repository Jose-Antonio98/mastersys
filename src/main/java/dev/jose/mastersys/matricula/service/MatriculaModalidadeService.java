package dev.jose.mastersys.matricula.service;

import dev.jose.mastersys.exception.RecursoJaCadastradoException;
import dev.jose.mastersys.matricula.domain.Matricula;
import dev.jose.mastersys.matricula.domain.MatriculaModalidade;
import dev.jose.mastersys.matricula.dto.MatriculaModalidadeRequest;
import dev.jose.mastersys.matricula.dto.MatriculaModalidadeResponse;
import dev.jose.mastersys.matricula.exception.MatriculaModalidadeNaoEncontradaException;
import dev.jose.mastersys.matricula.exception.MatriculaNaoEncontradaException;
import dev.jose.mastersys.matricula.repository.MatriculaModalidadeRepository;
import dev.jose.mastersys.matricula.repository.MatriculaRepository;
import dev.jose.mastersys.plano.domain.Plano;
import dev.jose.mastersys.plano.exception.PlanoNaoEncontradoException;
import dev.jose.mastersys.plano.repository.PlanosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
@Transactional(readOnly = true)
public class MatriculaModalidadeService {

    private final PlanosRepository planosRepository;
    private final MatriculaModalidadeRepository matriculaModalidadeRepository;
    private final MatriculaRepository matriculaRepository;

    public MatriculaModalidadeService(PlanosRepository planosRepository,
                                      MatriculaModalidadeRepository matriculaModalidadeRepository,
                                      MatriculaRepository matriculaRepository) {

        this.planosRepository = planosRepository;
        this.matriculaModalidadeRepository = matriculaModalidadeRepository;
        this.matriculaRepository = matriculaRepository;
    }

    @Transactional
    public MatriculaModalidadeResponse criarMatriculaModalidade(MatriculaModalidadeRequest request) {

        var matricula = buscarMatricula(request);
        var plano = buscarPlano(request);

        validarDuplicidade(request.matriculaId(), plano.getModalidade().getId());

        var matriculaModalidade = criarEntidade(plano, matricula);

        return MatriculaModalidadeResponse.fromEntity(matriculaModalidadeRepository.save(matriculaModalidade));
    }

    public List<MatriculaModalidadeResponse> listarPorMatricula(Long matriculaId) {
        if (!matriculaRepository.existsById(matriculaId)) {
            throw new MatriculaNaoEncontradaException(matriculaId);
        }

        return matriculaModalidadeRepository.findAllByMatriculaId(matriculaId)
                .stream().map(MatriculaModalidadeResponse::fromEntity).toList();
    }

    public MatriculaModalidadeResponse buscarPorId(Long id) {
        return MatriculaModalidadeResponse.fromEntity(buscarEntidadePorId(id));
    }

    @Transactional
    public void encerrarMatriculaModalidade(Long matriculaModalidadeId){
        var vinculo = buscarEntidadePorId(matriculaModalidadeId);

        if(vinculo.getDataFim() != null) {
            throw new RecursoJaCadastradoException("MatriculaModalidade", "O vinculo já foi encerrado");
        }

        vinculo.setDataFim(LocalDate.now());
    }
    
    // ==================
    // PRIVADOS
    // ==================
    private Matricula buscarMatricula(MatriculaModalidadeRequest request) {
        return matriculaRepository.findById(request.matriculaId())
                .orElseThrow(() -> new MatriculaNaoEncontradaException(request.matriculaId()));
    }

    private Plano buscarPlano(MatriculaModalidadeRequest request) {
        return planosRepository.findById(request.planoId())
                .orElseThrow(() -> new PlanoNaoEncontradoException(request.planoId()));
    }

    private void validarDuplicidade(Long matriculaId, Long modalidadeId) {
        if (matriculaModalidadeRepository.existsByMatriculaIdAndModalidadeIdAndDataFimIsNull(matriculaId, modalidadeId)) {
           throw new RecursoJaCadastradoException("MatriculaModalidade", "A matrícula já possui essa modalidade.");
        }
    }

    private MatriculaModalidade criarEntidade(Plano plano, Matricula matricula) {
        return new MatriculaModalidadeRequest(matricula.getId(), plano.getId()).toEntity(plano, matricula);
    }

    private MatriculaModalidade buscarEntidadePorId(Long id) {
        return matriculaModalidadeRepository.findById(id)
                .orElseThrow(() -> new MatriculaModalidadeNaoEncontradaException(id));
    }
}

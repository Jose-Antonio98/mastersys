package dev.jose.mastersys.service;

import dev.jose.mastersys.domain.Aluno;
import dev.jose.mastersys.dto.AlunoFiltroRequest;
import dev.jose.mastersys.dto.AlunoRequest;
import dev.jose.mastersys.dto.AlunoResponse;
import dev.jose.mastersys.dto.AlunoAtualizacaoRequest;
import dev.jose.mastersys.exception.AlunoNaoEncontradoException;
import dev.jose.mastersys.exception.RecursoJaCadastradoException;
import dev.jose.mastersys.repository.AlunoRepository;

import dev.jose.mastersys.specification.AlunoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    @Transactional
    public AlunoResponse cadastrar(AlunoRequest request){
        if (request.email() != null && alunoRepository.existsByEmailIgnoreCase(request.email())) {
            throw new RecursoJaCadastradoException("Aluno", request.email());
        }

        return AlunoResponse.fromEntity(alunoRepository.save(request.toEntity()));
    }

    public Page<AlunoResponse> listar(AlunoFiltroRequest filtro, Pageable pageable){
        return alunoRepository.findAll(AlunoSpecification.filtros(filtro), pageable)
                .map(AlunoResponse::fromEntity);
    }

    public AlunoResponse buscarPorId(Long id){
        return AlunoResponse.fromEntity(buscarEntityPorId(id));
    }

    @Transactional
    public AlunoResponse atualizar(Long id, AlunoRequest request){
        Aluno aluno = buscarEntityPorId(id);

        validarEmailDuplicado(id, request.email());

        request.preencher(aluno);

        return AlunoResponse.fromEntity(alunoRepository.save(aluno));
    }

    @Transactional
    public AlunoResponse atualizarParcial(Long id, AlunoAtualizacaoRequest request){
        Aluno aluno = buscarEntityPorId(id);

        validarEmailDuplicado(id, request.email());

        request.preencher(aluno);

        return AlunoResponse.fromEntity(alunoRepository.save(aluno));
    }

    @Transactional
    public void excluir(Long id){
        Aluno aluno = buscarEntityPorId(id);
        alunoRepository.delete(aluno);
    }

    private Aluno buscarEntityPorId(Long id){
        return alunoRepository.findById(id).orElseThrow(() -> new AlunoNaoEncontradoException(id));
    }

    private void validarEmailDuplicado(Long id, String email){
        if (email != null && alunoRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new RecursoJaCadastradoException("Aluno", email);
        }
    }
}

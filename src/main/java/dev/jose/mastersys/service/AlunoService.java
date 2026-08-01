package dev.jose.mastersys.service;

import dev.jose.mastersys.domain.Aluno;
import dev.jose.mastersys.dto.AlunoRequest;
import dev.jose.mastersys.dto.AlunoResponse;
import dev.jose.mastersys.exception.AlunoNaoEncontradoException;
import dev.jose.mastersys.exception.EmailJaCadastradoException;
import dev.jose.mastersys.repository.AlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;


    public AlunoResponse cadastrar(AlunoRequest request){
        if (request.email() != null && alunoRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException();
        }

        return AlunoResponse.fromEntity(alunoRepository.save(request.toEntity()));
    }

    public Page<AlunoResponse> listar(Pageable pageable){
        return alunoRepository.findAll(pageable).map(AlunoResponse::fromEntity);
    }

    public AlunoResponse buscarPorId(Long id){
        return AlunoResponse.fromEntity(buscarEntityPorId(id));
    }

    public AlunoResponse atualizar(long id, AlunoRequest request){
        Aluno aluno = buscarEntityPorId(id);
        request.preencher(aluno);
        return AlunoResponse.fromEntity(alunoRepository.save(request.toEntity()));
    }

    public void excluir(long id){
        Aluno aluno = buscarEntityPorId(id);
        alunoRepository.delete(aluno);
    }

    private Aluno buscarEntityPorId(Long id){
        return alunoRepository.findById(id).orElseThrow(AlunoNaoEncontradoException::new);
    }
}

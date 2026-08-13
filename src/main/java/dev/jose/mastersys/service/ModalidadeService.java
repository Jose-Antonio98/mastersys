package dev.jose.mastersys.service;

import dev.jose.mastersys.domain.Modalidade;
import dev.jose.mastersys.dto.ModalidadeRequest;
import dev.jose.mastersys.dto.ModalidadeResponse;
import dev.jose.mastersys.exception.*;
import dev.jose.mastersys.repository.ModalidadeRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ModalidadeService {

    private final ModalidadeRepository modalidadeRepository;

    public ModalidadeService(ModalidadeRepository modalidadeRepository) {
        this.modalidadeRepository = modalidadeRepository;
    }

    @Transactional
    public ModalidadeResponse cadastrarModalidade(ModalidadeRequest request) {
        validarModalidadeDuplicada(request.nome());

        return ModalidadeResponse.fromEntity(modalidadeRepository.save(request.toEntity()));
    }

    @Transactional
    public ModalidadeResponse atualizarModalidade(Long id, ModalidadeRequest request) {
        var modalidade = buscarEntityPorId(id);

        validarModalidadeDuplicadaAtualizacao(id, request.nome());

        request.preencher(modalidade);

        return ModalidadeResponse.fromEntity(modalidadeRepository.save(modalidade));
    }

    public ModalidadeResponse buscarPorId(Long id) {
        return ModalidadeResponse.fromEntity(buscarEntityPorId(id));
    }

    public List<ModalidadeResponse> listarModalidades(){
        return modalidadeRepository.findAll()
                .stream()
                .map(ModalidadeResponse::fromEntity)
                .toList();
    }

    public List<ModalidadeResponse> listarModalidadesAtivas(){
        return modalidadeRepository.findByAtivaTrue()
                .stream()
                .map(ModalidadeResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void inativarModalidade(Long id) {
        var modalidade = buscarEntityPorId(id);

        if (!modalidade.getAtiva()){
            throw new RecursoJaInativoException("Modalidade", modalidade.getNome());
        }
        alterarAtividade(modalidade, false);
    }

    @Transactional
    public void ativarModalidade(Long id) {
        var modalidade = buscarEntityPorId(id);

        if (modalidade.getAtiva()){
            throw new RecursoJaAtivoException("Modalidade", modalidade.getNome());
        }
        alterarAtividade(modalidade, true);
    }

    @Transactional
    public void removerModalidade(Long id) {
        modalidadeRepository.delete(buscarEntityPorId(id));
    }

    private Modalidade buscarEntityPorId(Long id){
        return modalidadeRepository.findById(id).orElseThrow(() -> new ModalidadeNaoEncontradaException(id));
    }

    private void alterarAtividade(Modalidade modalidade, boolean status) {
        modalidade.setAtiva(status);
        modalidadeRepository.save(modalidade);
    }

    private void validarModalidadeDuplicada(String nome) {
        if (modalidadeRepository.existsByNomeIgnoreCaseAndAcentos(nome)) {
            throw new RecursoJaCadastradoException("Modalidade", nome);
        }
    }

    private void validarModalidadeDuplicadaAtualizacao(long id, String nome) {
        if (modalidadeRepository.existsByNomeIgnoreCaseAndAcentosAndIdNot(nome, id)) {
            throw new RecursoJaCadastradoException("Modalidade", nome);
        }
    }
}

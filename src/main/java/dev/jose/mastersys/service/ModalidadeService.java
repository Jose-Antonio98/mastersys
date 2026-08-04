package dev.jose.mastersys.service;

import dev.jose.mastersys.domain.Modalidade;
import dev.jose.mastersys.dto.ModalidadeRequest;
import dev.jose.mastersys.dto.ModalidadeResponse;
import dev.jose.mastersys.exception.*;
import dev.jose.mastersys.repository.ModalidadeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModalidadeService {

    private final ModalidadeRepository modalidadeRepository;

    public ModalidadeService(ModalidadeRepository modalidadeRepository) {
        this.modalidadeRepository = modalidadeRepository;
    }

    public ModalidadeResponse cadastrarModalidade(ModalidadeRequest request) {
        if (modalidadeRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new ModalidadeJaCadastradaException(request.nome());
        }

        return ModalidadeResponse.fromEntity(modalidadeRepository.save(request.toEntity()));
    }

    public ModalidadeResponse atualizarModalidade(Long id, ModalidadeRequest request) {
        var modalidade = buscarEntityPorId(id);
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

    public void inativarModalidade(Long id) {
        var modalidade = buscarEntityPorId(id);

        if (!modalidade.getAtiva()){
            throw new ModalidadeJaInativa(modalidade.getNome());
        }
        alterarAtividade(modalidade, false);
    }

    public void ativarModalidade(Long id) {
        var modalidade = buscarEntityPorId(id);

        if (modalidade.getAtiva()){
            throw new ModalidadeJaAtiva(modalidade.getNome());
        }
        alterarAtividade(modalidade, true);
    }



    private Modalidade buscarEntityPorId(Long id){
        return modalidadeRepository.findById(id).orElseThrow(() -> new ModalidadeNaoEncontradaException(id));
    }

    private void alterarAtividade(Modalidade modalidade, boolean status) {
        modalidade.setAtiva(status);
        modalidadeRepository.save(modalidade);
    }

}

package dev.jose.mastersys.service;

import dev.jose.mastersys.domain.Modalidade;
import dev.jose.mastersys.domain.Plano;
import dev.jose.mastersys.dto.PlanoAtualizacaoRequest;
import dev.jose.mastersys.dto.PlanoRequest;
import dev.jose.mastersys.dto.PlanoResponse;
import dev.jose.mastersys.exception.*;
import dev.jose.mastersys.repository.ModalidadeRepository;
import dev.jose.mastersys.repository.PlanosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class PlanoService {

    private final PlanosRepository planosRepository;
    private final ModalidadeRepository modalidadeRepository;

    public PlanoService(PlanosRepository planosRepository, ModalidadeRepository modalidadeRepository) {
        this.planosRepository = planosRepository;
        this.modalidadeRepository = modalidadeRepository;
    }

    @Transactional
    public PlanoResponse cadastrarPlano(PlanoRequest request){

        var modalidade = buscarModalidadePorId(request.modalidadeId());

        validarPlanoDuplicado(request.nome(), request.modalidadeId());

        var plano = request.toEntity();

        plano.setModalidade(modalidade);

        return PlanoResponse.fromEntity(planosRepository.save(plano));
    }

    @Transactional
    public PlanoResponse atualizarPlano(Long planoId, PlanoAtualizacaoRequest request){

        var plano = buscarEntityPorId(planoId);

        validarPlanoDuplicadoAtualizacao(plano, request.nome());

        request.preencher(plano);

        return PlanoResponse.fromEntity(planosRepository.save(plano));
    }

    public PlanoResponse buscarPlanoPorId(Long id){
        return PlanoResponse.fromEntity(buscarEntityPorId(id));
    }

    public List<PlanoResponse> listarPlanosPorModalidade(Long modalidadeId){
        buscarModalidadePorId(modalidadeId);

        return  planosRepository.findAllByModalidadeId(modalidadeId)
                .stream()
                .map(PlanoResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void inativarPlano(Long id) {
        var plano = buscarEntityPorId(id);

        if (!plano.getAtivo()){
            throw new RecursoJaInativoException("Plano", plano.getNome());
        }
        alterarAtividade(plano, false);
    }

    @Transactional
    public void ativarPlano(Long id) {
        var plano = buscarEntityPorId(id);

        if (plano.getAtivo()){
            throw new RecursoJaAtivoException("Plano", plano.getNome());
        }
        alterarAtividade(plano, true);
    }

    @Transactional
    public void removerPlano(Long id) {
        planosRepository.delete(buscarEntityPorId(id));
    }


    private void validarPlanoDuplicado(String nome, Long idModalidade){

        if(planosRepository.existsByNomeIgnoreCaseAndAcentos(nome, idModalidade)){
            throw new RecursoJaCadastradoException("Plano", nome);
        }
    }

    private void validarPlanoDuplicadoAtualizacao(Plano plano, String novoNome) {

        if (planosRepository.existsByNomeIgnoreCaseAndAcentosAndIdNot(novoNome, plano.getModalidade().getId(),
                plano.getId())) {

            throw new RecursoJaCadastradoException("Plano", novoNome);
        }
    }

    private Modalidade buscarModalidadePorId(Long id) {
        return modalidadeRepository.findById(id)
                .orElseThrow(() -> new ModalidadeNaoEncontradaException(id));
    }

    private Plano buscarEntityPorId(Long id){
        return planosRepository.findById(id).orElseThrow(() -> new PlanoNaoEncontradoException(id));
    }

    private void alterarAtividade(Plano plano, boolean status) {
        plano.setAtivo(status);
        planosRepository.save(plano);
    }
}

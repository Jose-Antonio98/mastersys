package dev.jose.mastersys.controller;

import dev.jose.mastersys.dto.ModalidadeRequest;
import dev.jose.mastersys.dto.ModalidadeResponse;
import dev.jose.mastersys.service.ModalidadeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/modalidades")
public class ModalidadeController {

    private final ModalidadeService modalidadeService;

    public ModalidadeController(ModalidadeService modalidadeService) {
        this.modalidadeService = modalidadeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ModalidadeResponse cadastrar(@RequestBody @Valid ModalidadeRequest modalidadeRequest) {
        return modalidadeService.cadastrarModalidade(modalidadeRequest);
    }

    @PutMapping("/{id}")
    public ModalidadeResponse atualizar(@PathVariable Long id,
                                        @RequestBody @Valid ModalidadeRequest modalidadeRequest) {
        return modalidadeService.atualizarModalidade(id, modalidadeRequest);
    }
    @GetMapping
    public List<ModalidadeResponse> listar() {
        return modalidadeService.listarModalidades();
    }

    @GetMapping("/disponiveis")
    public List<ModalidadeResponse> listarDisponiveis() {
        return modalidadeService.listarModalidadesAtivas();
    }

    @GetMapping("/{id}")
    public ModalidadeResponse buscarPorId(@PathVariable Long id) {
        return modalidadeService.buscarPorId(id);
    }

    @PatchMapping("/{id}/ativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ativarModalidade(@PathVariable Long id) {
        modalidadeService.ativarModalidade(id);
    }

    @PatchMapping("/{id}/inativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativarModalidade(@PathVariable Long id) {
        modalidadeService.inativarModalidade(id);
    }
}

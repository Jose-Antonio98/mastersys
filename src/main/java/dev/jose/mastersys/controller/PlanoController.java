package dev.jose.mastersys.controller;

import dev.jose.mastersys.dto.PlanoAtualizacaoRequest;
import dev.jose.mastersys.dto.PlanoRequest;
import dev.jose.mastersys.dto.PlanoResponse;
import dev.jose.mastersys.service.PlanoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planos")
public class PlanoController {

    private final PlanoService planoService;

    public PlanoController(PlanoService planoService) {
        this.planoService = planoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlanoResponse cadastrarPlano(@RequestBody @Valid PlanoRequest request){
        return planoService.cadastrarPlano(request);
    }

    @PutMapping("/{id}")
    public PlanoResponse atualizarPlano(@PathVariable Long id, @RequestBody @Valid PlanoAtualizacaoRequest request){
        return planoService.atualizarPlano(id, request);
    }

    @GetMapping("/{id}")
    public PlanoResponse buscarPlanoPorId(@PathVariable Long id){
        return planoService.buscarPlanoPorId(id);
    }

    @GetMapping("/modalidade/{id}")
    public List<PlanoResponse> buscarPlanos(@PathVariable Long id){
        return planoService.listarPlanosPorModalidade(id);
    }

    @PatchMapping("/{id}/ativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ativarPlano(@PathVariable Long id) {
        planoService.ativarPlano(id);
    }

    @PatchMapping("/{id}/inativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativarPlano(@PathVariable Long id) {
        planoService.inativarPlano(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerPlano(@PathVariable Long id) {
        planoService.removerPlano(id);
    }
}

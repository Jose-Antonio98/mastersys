package dev.jose.mastersys.controller;

import dev.jose.mastersys.dto.AlunoFiltroRequest;
import dev.jose.mastersys.dto.AlunoRequest;
import dev.jose.mastersys.dto.AlunoResponse;
import dev.jose.mastersys.dto.AlunoAtualizacaoRequest;
import dev.jose.mastersys.service.AlunoService;

import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController (AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlunoResponse cadastrar(@RequestBody @Valid AlunoRequest request){
        return alunoService.cadastrar(request);
    }

    @GetMapping
    public Page<AlunoResponse> listar(@ParameterObject AlunoFiltroRequest filtro, @ParameterObject Pageable pageable){
        return alunoService.listar(filtro, pageable);
    }

    @GetMapping("/{id}")
    public AlunoResponse buscarPorId(@PathVariable Long id){
        return alunoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public AlunoResponse atualizar(@PathVariable Long id, @RequestBody @Valid AlunoRequest request){
        return alunoService.atualizar(id, request);
    }

    @PatchMapping("/{id}")
    public AlunoResponse atualizarParcial(@PathVariable Long id, @RequestBody @Valid AlunoAtualizacaoRequest request){
        return alunoService.atualizarParcial(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        alunoService.excluir(id);
    }

}

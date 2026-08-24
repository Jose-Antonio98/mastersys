package dev.jose.mastersys.controller;

import dev.jose.mastersys.dto.MatriculaFiltroRequest;
import dev.jose.mastersys.dto.MatriculaRequest;
import dev.jose.mastersys.dto.MatriculaResponse;
import dev.jose.mastersys.service.MatriculaService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MatriculaResponse matricular(@RequestBody @Valid MatriculaRequest matriculaRequest){
        return matriculaService.criarMatricula(matriculaRequest);
    }

    @GetMapping
    public Page<MatriculaResponse> listarMatriculas(@ParameterObject MatriculaFiltroRequest filtro,
                                                    @ParameterObject Pageable pageable){
        return matriculaService.listar(filtro, pageable);
    }
    @GetMapping("/{id}")
    public MatriculaResponse buscarMatriculaPorId(@PathVariable Long id){
        return matriculaService.buscarPorId(id);
    }

    @PatchMapping("{id}/vencimento")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarDiaVencimento(@PathVariable Long id, @RequestBody Integer diaVencimento){
        matriculaService.alterarDiaVencimento(id, diaVencimento);
    }

    @PatchMapping("{id}/cancelamento")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarMatricula(@PathVariable Long id){
        matriculaService.cancelarMatricula(id);
    }

    @PatchMapping("{id}/encerramento")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void encerrarMatricula(@PathVariable Long id){
        matriculaService.encerrarMatricula(id);
    }

    @PatchMapping("{id}/ativacao")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ativarMatricula(@PathVariable Long id){
        matriculaService.ativarMatricula(id);
    }
}

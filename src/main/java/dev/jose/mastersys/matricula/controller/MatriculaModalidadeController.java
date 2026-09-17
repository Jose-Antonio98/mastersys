package dev.jose.mastersys.matricula.controller;

import dev.jose.mastersys.matricula.dto.MatriculaModalidadeRequest;
import dev.jose.mastersys.matricula.dto.MatriculaModalidadeResponse;
import dev.jose.mastersys.matricula.service.MatriculaModalidadeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matriculas")
public class MatriculaModalidadeController {

    private final MatriculaModalidadeService matriculaModalidadeService;

    public MatriculaModalidadeController(MatriculaModalidadeService matriculaModalidadeService) {
        this.matriculaModalidadeService = matriculaModalidadeService;
    }

    @PostMapping("/modalidades")
    @ResponseStatus(HttpStatus.CREATED)
    public MatriculaModalidadeResponse criarMatriculaModalidade(
            @RequestBody @Valid MatriculaModalidadeRequest request) {

        return matriculaModalidadeService.criarMatriculaModalidade(request);
    }

    @GetMapping("/{matriculaId}/modalidades")
    public List<MatriculaModalidadeResponse> listarPorMatricula(@PathVariable Long matriculaId) {
        return matriculaModalidadeService.listarPorMatricula(matriculaId);
    }

    @GetMapping("/modalidades/{id}")
    public MatriculaModalidadeResponse buscarPorId(@PathVariable Long id) {
        return matriculaModalidadeService.buscarPorId(id);
    }

    @PatchMapping("/modalidades/{id}/encerramento")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void encerrarMatriculaModalidade(@PathVariable Long id) {
        matriculaModalidadeService.encerrarMatriculaModalidade(id);
    }

}

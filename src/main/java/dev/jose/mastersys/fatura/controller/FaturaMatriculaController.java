package dev.jose.mastersys.fatura.controller;

import dev.jose.mastersys.fatura.dto.FaturaFiltroRequest;
import dev.jose.mastersys.fatura.dto.FaturaMatriculaRequest;
import dev.jose.mastersys.fatura.dto.FaturaMatriculaResponse;
import dev.jose.mastersys.fatura.service.FaturaMatriculaService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/faturas")
public class FaturaMatriculaController {

    private final FaturaMatriculaService faturaMatriculaService;

    public FaturaMatriculaController(FaturaMatriculaService faturaMatriculaService) {
        this.faturaMatriculaService = faturaMatriculaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FaturaMatriculaResponse cadastrarFatura(@RequestBody @Valid FaturaMatriculaRequest request) {
        return faturaMatriculaService.criarFatura(request);
    }

    @GetMapping
    public Page<FaturaMatriculaResponse> buscarFaturas( @ParameterObject FaturaFiltroRequest filtro,
                                                       @ParameterObject @PageableDefault (size = 10, sort = "id")
                                                       Pageable pageable) {
        return faturaMatriculaService.listarFaturas(filtro, pageable);
    }

    @GetMapping("/{id}")
    public FaturaMatriculaResponse buscarFaturaPorId(@PathVariable Long id) {
        return faturaMatriculaService.buscarFaturaPorId(id);
    }

    @PatchMapping("/{id}/pagar")
    public FaturaMatriculaResponse pagarFatura(@PathVariable Long id) {
        return faturaMatriculaService.pagarFatura(id);
    }

    @PatchMapping("/{id}/cancelar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarFatura(@PathVariable Long id) {
        faturaMatriculaService.cancelarFatura(id);
    }
}

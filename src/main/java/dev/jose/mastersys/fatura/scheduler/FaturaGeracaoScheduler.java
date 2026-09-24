package dev.jose.mastersys.fatura.scheduler;

import dev.jose.mastersys.fatura.service.FaturaMatriculaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FaturaGeracaoScheduler {

    private final FaturaMatriculaService faturaMatriculaService;

    public FaturaGeracaoScheduler(FaturaMatriculaService faturaMatriculaService) {
        this.faturaMatriculaService = faturaMatriculaService;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void gerarFatura() {
        faturaMatriculaService.gerarFaturasDoPeriodo();
    }
}

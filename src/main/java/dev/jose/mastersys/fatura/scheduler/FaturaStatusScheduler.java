package dev.jose.mastersys.fatura.scheduler;

import dev.jose.mastersys.fatura.domain.FaturaMatricula;
import dev.jose.mastersys.fatura.domain.enums.StatusFatura;
import dev.jose.mastersys.fatura.repository.FaturaMatriculaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class FaturaStatusScheduler {
    private final FaturaMatriculaRepository faturaMatriculaRepository;

    public FaturaStatusScheduler(FaturaMatriculaRepository repository) {
        this.faturaMatriculaRepository = repository;
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void atualizarFaturasVencidas() {
        var faturas = faturaMatriculaRepository.findAllByStatusFaturaAndDataVencimentoBefore(
                StatusFatura.ABERTA, LocalDate.now());

        faturas.forEach(FaturaMatricula::marcarComoVencida);
        faturaMatriculaRepository.saveAll(faturas);
    }
}

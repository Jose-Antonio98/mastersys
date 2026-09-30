package dev.jose.mastersys.fatura.exception;

import java.time.LocalDate;

public class FaturaDuplicadaException extends RuntimeException {

    public FaturaDuplicadaException(Long matriculaId, LocalDate dataVencimento) {
        super(String.format(
                "Já existe uma fatura para a matrícula '%s' com vencimento em '%s'.",
                matriculaId,
                dataVencimento
        ));
    }
}

package dev.jose.mastersys.fatura.exception;

public class FaturaSemModalidadeAtivaException extends RuntimeException {

    public FaturaSemModalidadeAtivaException(Long matriculaId) {
        super(String.format(
                "Não é possível gerar fatura para a matrícula '%s' sem modalidade ativa.",
                matriculaId
        ));
    }
}

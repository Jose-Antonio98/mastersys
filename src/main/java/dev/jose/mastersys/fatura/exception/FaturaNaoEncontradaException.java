package dev.jose.mastersys.fatura.exception;

public class FaturaNaoEncontradaException extends RuntimeException {

    public FaturaNaoEncontradaException(Long id) {
        super(String.format("A fatura com ID '%s' não foi encontrada;", id));
    }
}

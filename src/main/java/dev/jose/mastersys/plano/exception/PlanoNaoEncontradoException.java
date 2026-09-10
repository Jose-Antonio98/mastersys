package dev.jose.mastersys.plano.exception;

public class PlanoNaoEncontradoException extends RuntimeException {

    public PlanoNaoEncontradoException(Long id) {
        super(String.format("O plano com ID '%s' não foi encontrado;", id));
    }
}

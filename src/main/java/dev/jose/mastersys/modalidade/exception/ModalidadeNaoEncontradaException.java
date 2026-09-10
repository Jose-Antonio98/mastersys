package dev.jose.mastersys.modalidade.exception;

public class ModalidadeNaoEncontradaException extends RuntimeException {

    public ModalidadeNaoEncontradaException(Long id) {
        super(String.format("A modalidade com ID '%s' não foi encontrada;", id));
    }
}

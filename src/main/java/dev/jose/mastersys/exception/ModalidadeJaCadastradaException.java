package dev.jose.mastersys.exception;

public class ModalidadeJaCadastradaException extends RuntimeException {

    public ModalidadeJaCadastradaException(String nome) {
        super(String.format("A modalidade '%s' já está cadastrada.", nome));
    }
}

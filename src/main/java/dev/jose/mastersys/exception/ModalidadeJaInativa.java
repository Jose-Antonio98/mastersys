package dev.jose.mastersys.exception;

public class ModalidadeJaInativa extends RuntimeException {

    public ModalidadeJaInativa(String nome) {
        super(String.format("A modalidade '%s' já está inativa.", nome));
    }
}

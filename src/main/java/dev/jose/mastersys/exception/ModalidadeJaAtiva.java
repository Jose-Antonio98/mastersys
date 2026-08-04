package dev.jose.mastersys.exception;

public class ModalidadeJaAtiva extends RuntimeException {

    public ModalidadeJaAtiva(String nome) {
        super(String.format("A modalidade '%s' já está ativa.", nome));
    }
}

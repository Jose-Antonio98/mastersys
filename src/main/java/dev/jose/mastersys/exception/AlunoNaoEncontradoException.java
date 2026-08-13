package dev.jose.mastersys.exception;

public class AlunoNaoEncontradoException extends RuntimeException{

    public AlunoNaoEncontradoException(Long id) {
        super(String.format("O Aluno com ID '%s' não foi encontrado;", id));
    }
}

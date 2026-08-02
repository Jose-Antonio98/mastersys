package dev.jose.mastersys.exception;

public class AlunoNaoEncontradoException extends RuntimeException{

    public AlunoNaoEncontradoException(Long id) {
        super("Aluno não encontrado. Id: " + id);
    }
}

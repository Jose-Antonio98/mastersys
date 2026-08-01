package dev.jose.mastersys.exception;

public class AlunoNaoEncontradoException extends RuntimeException{

    public AlunoNaoEncontradoException(){
        super("Aluno não encontrado");
    }
}

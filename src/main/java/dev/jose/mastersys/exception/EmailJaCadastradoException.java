package dev.jose.mastersys.exception;

public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException() {
        super("Já existe um aluno com esse email");
    }
}

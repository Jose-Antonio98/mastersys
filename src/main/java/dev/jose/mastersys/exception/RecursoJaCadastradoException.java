package dev.jose.mastersys.exception;

public class RecursoJaCadastradoException extends RuntimeException {

    public RecursoJaCadastradoException(String recurso, String valor) {
        super(String.format("%s '%s' já está cadastrado.", recurso, valor));
    }

}

package dev.jose.mastersys.exception;

public class RecursoJaAtivoException extends RuntimeException {

    public RecursoJaAtivoException(String recurso, String valor) {
        super(String.format("%s '%s' já está com status ativo.", recurso, valor));
    }

}

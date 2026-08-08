package dev.jose.mastersys.exception;

public class RecursoJaInativoException extends RuntimeException {

    public RecursoJaInativoException(String recurso, String valor) {
        super(String.format("%s '%s' já está com status inativo.", recurso, valor));
    }

}

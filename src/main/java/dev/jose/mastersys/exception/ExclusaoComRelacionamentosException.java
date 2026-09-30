package dev.jose.mastersys.exception;

public class ExclusaoComRelacionamentosException extends RuntimeException {

    public ExclusaoComRelacionamentosException(String recurso, Throwable cause) {
        super("Não é possível excluir " + recurso + " porque existem registros relacionados.", cause);
    }
}

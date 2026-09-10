package dev.jose.mastersys.matricula.exception;

public class MatriculaNaoEncontradaException extends RuntimeException {

    public MatriculaNaoEncontradaException(Long id){
        super(String.format("A matricula com ID '%s' não foi encontrada;", id));
    }
}

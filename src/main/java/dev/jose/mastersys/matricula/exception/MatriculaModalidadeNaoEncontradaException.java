package dev.jose.mastersys.matricula.exception;

public class MatriculaModalidadeNaoEncontradaException extends RuntimeException {

    public MatriculaModalidadeNaoEncontradaException(Long id){
        super(String.format("A matricula na modalidade com ID '%s' não foi encontrada;", id));
    }
}

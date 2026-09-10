package dev.jose.mastersys.factory;


import dev.jose.mastersys.matricula.dto.MatriculaRequest;


public class MatriculaTestFactory {

    private MatriculaTestFactory() {}

    public static MatriculaRequest matriculaRequest() {

        return new MatriculaRequest(
                1L,
                10
        );
    }
}

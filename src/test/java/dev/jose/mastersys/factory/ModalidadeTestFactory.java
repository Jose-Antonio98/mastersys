package dev.jose.mastersys.factory;


import dev.jose.mastersys.dto.ModalidadeRequest;

public class ModalidadeTestFactory {

    private ModalidadeTestFactory() {}

    public static ModalidadeRequest modalidadeRequest() {

        return new ModalidadeRequest(
                 "Academia"
        );
    }

    public static ModalidadeRequest modalidadeRequestAtualizado() {

        return new ModalidadeRequest(
                "Boxe"
        );
    }
}

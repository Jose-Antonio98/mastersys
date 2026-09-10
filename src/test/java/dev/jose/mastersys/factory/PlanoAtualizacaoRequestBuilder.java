package dev.jose.mastersys.factory;

import dev.jose.mastersys.plano.dto.PlanoAtualizacaoRequest;

import java.math.BigDecimal;

public class PlanoAtualizacaoRequestBuilder {

    private String  nome;
    private BigDecimal valor;

    public PlanoAtualizacaoRequestBuilder nome(String nome) {
        this.nome = nome;
        return this;
    }

    public PlanoAtualizacaoRequestBuilder valor(BigDecimal valor) {
        this.valor = valor;
        return this;
    }

    public PlanoAtualizacaoRequest build() {
        return new PlanoAtualizacaoRequest(nome, valor);
    }
}

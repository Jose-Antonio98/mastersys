package dev.jose.mastersys.factory;

import dev.jose.mastersys.domain.enums.Sexo;
import dev.jose.mastersys.dto.AlunoAtualizacaoRequest;

import java.time.LocalDate;

public class AlunoAtualizacaoRequestBuilder {

    private String nome;
    private LocalDate dataNascimento;
    private Sexo sexo;
    private String telefone;
    private String celular;
    private String email;
    private String observacao;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;

    public AlunoAtualizacaoRequestBuilder nome(String nome) {
        this.nome = nome;
        return this;
    }

    public AlunoAtualizacaoRequestBuilder dataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
        return this;
    }

    public AlunoAtualizacaoRequestBuilder email(String email) {
        this.email = email;
        return this;
    }


    public AlunoAtualizacaoRequest build() {
        return new AlunoAtualizacaoRequest(
                nome,
                dataNascimento,
                sexo,
                telefone,
                celular,
                email,
                observacao,
                endereco,
                numero,
                complemento,
                bairro,
                cidade,
                estado,
                cep
        );
    }
}

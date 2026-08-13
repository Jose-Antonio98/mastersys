package dev.jose.mastersys.dto;

import dev.jose.mastersys.domain.Aluno;
import dev.jose.mastersys.domain.enums.Sexo;
import jakarta.validation.constraints.Email;

import java.time.LocalDate;

public record AlunoAtualizacaoRequest(
        String nome,
        LocalDate dataNascimento,
        Sexo sexo,
        String telefone,
        String celular,
        @Email(message = "Entre com um e-mail válido")
        String email,
        String observacao,
        String endereco,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep
) {

    public void preencher(Aluno aluno) {

        if (nome != null) aluno.setNome(nome);
        if (dataNascimento != null) aluno.setDataNascimento(dataNascimento);
        if (sexo != null) aluno.setSexo(sexo);
        if (telefone != null) aluno.setTelefone(telefone);
        if (celular != null) aluno.setCelular(celular);
        if (email != null) aluno.setEmail(email);
        if (observacao != null) aluno.setObservacao(observacao);
        if (endereco != null) aluno.setEndereco(endereco);
        if (numero != null) aluno.setNumero(numero);
        if (complemento != null) aluno.setComplemento(complemento);
        if (bairro != null) aluno.setBairro(bairro);
        if (cidade != null) aluno.setCidade(cidade);
        if (estado != null) aluno.setEstado(estado);
        if (cep != null) aluno.setCep(cep);
    }
}

package dev.jose.mastersys.dto;

import dev.jose.mastersys.domain.Aluno;
import dev.jose.mastersys.domain.enums.Sexo;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record AlunoRequest(

        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 3, max = 150, message = "O nome deve conter entre 3 a 150 caracteres.")
        String nome,

        @Past(message = "A data de nascimento deve estar no passado.")
        LocalDate dataNascimento,

        @NotNull(message = "O sexo é obrigatório.")
        Sexo sexo,

        @Size(max = 20, message = "O telefone deve conter no máximo 20 caracteres.")
        String telefone,

        @Size(max = 20, message = "O celular deve conter no máximo 20 caracteres.")
        String celular,

        @Email(message = "Entre com um e-mail válido")
        @Size(max = 150, message = "O email deve conter no máximo 150 caracteres.")
        String email,

        String observacao,

        @Size(max = 150, message = "O endereço deve conter no máximo 150 caracteres.")
        String endereco,

        @Size(max = 20, message = "O numero não deve ultrapassar 20 caracteres.")
        String numero,

        @Size(max = 100, message = "O complemento não deve ultrapassar 100 caracteres.")
        String complemento,

        @Size(max = 100, message = "O bairro não deve ultrapassar 100 caracteres.")
        String bairro,

        @Size(max = 100, message = "O cidade não deve ultrapassar 100 caracteres.")
        String cidade,

        @Size(max = 2, message = "O estado não deve ultrapassar 2 caracteres.")
        String estado,

        @Size(max = 20, message = "O CEP não deve ultrapassar 20 caracteres.")
        String cep

) {

    public Aluno toEntity() {
        var aluno = new Aluno();
        preencher(aluno);
        return aluno;
    }

    public void preencher (Aluno aluno) {
        aluno.setNome(nome);
        aluno.setDataNascimento(dataNascimento);
        aluno.setSexo(sexo);
        aluno.setTelefone(telefone);
        aluno.setCelular(celular);
        aluno.setEmail(email);
        aluno.setObservacao(observacao);
        aluno.setEndereco(endereco);
        aluno.setNumero(numero);
        aluno.setComplemento(complemento);
        aluno.setBairro(bairro);
        aluno.setCidade(cidade);
        aluno.setEstado(estado);
        aluno.setCep(cep);
    }
}

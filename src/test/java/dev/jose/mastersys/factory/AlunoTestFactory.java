package dev.jose.mastersys.factory;

import dev.jose.mastersys.domain.enums.Sexo;
import dev.jose.mastersys.dto.AlunoRequest;

import java.time.LocalDate;


public class AlunoTestFactory {

    private AlunoTestFactory(){}

    public static AlunoRequest alunoRequest(){

        return new AlunoRequest(
                "José da Silva",
                LocalDate.of(1995, 5, 10),
                Sexo.M,
                "123456789",
                "12999999999",
                "jose@email.com",
                "Aluno de teste",
                "Rua dos Testes",
                "123",
                "Apto 10",
                "Centro",
                "Cruzeiro",
                "SP",
                "12700000"
        );
    }

    public static AlunoRequest alunoRequestAtualizar(){

        return new AlunoRequest(
                "Lucas da silva",
                LocalDate.of(1995, 5, 10),
                Sexo.M,
                "987654321",
                "12888888888",
                "lucas@email.com",
                "Aluno de teste atualizado",
                "Rua dos Testes atualizados",
                "456",
                "Apto 20",
                "Novo bairro",
                "Ubatuba",
                "SP",
                "12711111"
        );
    }

}

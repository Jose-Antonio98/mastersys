package dev.jose.mastersys.domain;

import dev.jose.mastersys.domain.enums.Sexo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "alunos")
@Getter @Setter
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(length = 1)
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

    @Column(name = "estado", length = 2)
    private String estado;
    private String cep;



    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}

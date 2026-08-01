create table alunos(
    id BIGSERIAL PRIMARY KEY,
    nome varchar(150) NOT NULL,
    data_nascimento Date,
    sexo varchar(1) check (sexo IN ('M', 'F')),
    telefone varchar(30),
    celular varchar(30),
    email varchar(150),
    observacao TEXT,
    endereco varchar(150),
    numero varchar(20),
    complemento varchar(100),
    bairro varchar(100),
    cidade varchar(100),
    estado varchar(20),
    cep varchar(20),
    criado_em timestamp not null default current_timestamp,
    atualizado_em timestamp
);

create table modalidades(
    id BIGSERIAL PRIMARY KEY,
    nome varchar(100) not null unique,
    ativa BOOLEAN not null default true
);

create table graduacoes
(
    id BIGSERIAL PRIMARY KEY,
    modalidade_id BIGINT not null references modalidades(id),
    nome varchar(100) not null,
    UNIQUE (modalidade_id, nome)
);

create table planos(
    id BIGSERIAL PRIMARY KEY,
    modalidade_id BIGINT not null references modalidades(id),
    nome varchar(100) not null,
    valor_mensal numeric(10,2) not null check (valor_mensal >= 0),
    ativo BOOLEAN not null default true,
    UNIQUE (modalidade_id, nome)
);

create table matriculas(
    id BIGSERIAL PRIMARY KEY,
    aluno_id BIGINT not null references alunos(id),
    data_matricula date not null default current_date,
    dia_vencimento integer not null check (dia_vencimento between 1 and 31),
    data_encerramento date,
    status varchar(20) not null default 'ATIVA',
    check (status in ('ATIVA', 'ENCERRADA', 'CANCELADA'))
);

create table matriculas_modalidades(
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT not null references matriculas(id),
    modalidade_id BIGINT not null references modalidades(id),
    graduacao_id BIGINT references graduacoes (id),
    plano_id BIGINT not null references planos(id),
    data_inicio date not null default current_date,
    data_fim date,
    unique (matricula_id, modalidade_id)

);

create table faturas_matriculas(
  id BIGSERIAL primary key,
  matricula_id BIGINT not null references matriculas(id),
  data_vencimento date not null,
  valor numeric(10, 2) not null check (valor >= 0),
  data_pagamento timestamp,
  data_cancelamento date,
  status varchar(20) not null default 'ABERTA',
  check (status in ('ABERTA', 'PAGA', 'CANCELADA', 'VENCIDA')),
  unique (matricula_id, data_vencimento)
);

create table assiduidade(
    id BIGSERIAL primary key,
    matricula_id BIGINT not null references matriculas(id),
    data_entrada timestamp not null default current_timestamp,
    data_saida timestamp
)
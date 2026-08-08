insert into modalidades (nome) values
('Musculação'),
('Funcional'),
('Jiu-Jitsu'),
('Muay Thai'),
('Pilates');

insert into planos (modalidade_id, nome, valor_mensal)
select id, 'Mensal', 120.00 from modalidades where nome = 'Musculação';

insert into planos (modalidade_id, nome, valor_mensal)
select id, 'Trimestral', 330.00 from modalidades where nome = 'Musculação';

insert into planos (modalidade_id, nome, valor_mensal)
select id, 'Mensal', 150.00 from modalidades where nome = 'Funcional';

insert into planos (modalidade_id, nome, valor_mensal)
select id, 'Mensal', 180.00 from modalidades where nome = 'Jiu-Jitsu';
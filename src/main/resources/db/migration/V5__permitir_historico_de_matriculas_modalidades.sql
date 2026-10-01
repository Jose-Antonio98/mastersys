ALTER TABLE matriculas_modalidades
    DROP CONSTRAINT matriculas_modalidades_matricula_id_modalidade_id_key;

CREATE UNIQUE INDEX uk_matricula_modalidade_ativa
    ON matriculas_modalidades (matricula_id, modalidade_id)
    WHERE data_fim IS NULL;
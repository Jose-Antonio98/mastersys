package dev.jose.mastersys.relatorio.repository;

import dev.jose.mastersys.fatura.domain.FaturaMatricula;
import dev.jose.mastersys.relatorio.projection.AlunosPorCidadeProjection;
import dev.jose.mastersys.relatorio.projection.FaturaRelatorioProjection;
import dev.jose.mastersys.relatorio.projection.FaturamentoMensalProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RelatorioRepository extends Repository<FaturaMatricula, Long> {

    @Query(value = """
             SELECT 
                TO_CHAR(data_pagamento, 'YYYY-MM') AS mes,
                SUM(valor) AS total        
             FROM faturas_matriculas
             WHERE status = 'PAGA'
             GROUP BY TO_CHAR(data_pagamento, 'YYYY-MM')
             ORDER BY mes
            """,
            nativeQuery = true
    )
    List<FaturamentoMensalProjection> faturamentoMensal();


    @Query(value = """
             SELECT cidade AS cidade, count(*) as quantidade      
             FROM alunos
             WHERE cidade = :cidade
             GROUP BY cidade
            """,
            nativeQuery = true
    )
    List<AlunosPorCidadeProjection> alunosPorCidade(@Param("cidade") String cidade);


    @Query(value = """
             SELECT 
                m.id AS "matriculaId",
                a.nome AS "alunoNome",
                f.data_vencimento AS "dataVencimento",
                f.data_pagamento AS "dataPagamento",
                f.data_cancelamento AS "dataCancelamento",
                f.valor AS "valor",
                f.status AS "statusFatura"
             FROM faturas_matriculas f 
             JOIN matriculas m ON m.id = f.matricula_id
             JOIN alunos a ON a.id = m.aluno_id
             WHERE f.status IN ('ABERTA', 'VENCIDA')
             ORDER BY f.data_vencimento ASC
            """,
            nativeQuery = true
    )
    List<FaturaRelatorioProjection> faturasEmAberto();

    @Query(value = """
             SELECT 
                m.id AS "matriculaId",
                a.nome AS "alunoNome",
                f.data_vencimento AS "dataVencimento",
                f.data_pagamento AS "dataPagamento",
                f.data_cancelamento AS "dataCancelamento",
                f.valor AS "valor",
                f.status AS "statusFatura"
             FROM faturas_matriculas f 
             JOIN matriculas m ON m.id = f.matricula_id
             JOIN alunos a ON a.id = m.aluno_id
             WHERE f.status = :status
             ORDER BY f.data_vencimento ASC
            """,
            nativeQuery = true
    )
    List<FaturaRelatorioProjection> faturasPorStatus(@Param("status") String status);



}

package dev.jose.mastersys.repository;

import dev.jose.mastersys.aluno.domain.Aluno;
import dev.jose.mastersys.aluno.domain.enums.Sexo;
import dev.jose.mastersys.aluno.repository.AlunoRepository;
import dev.jose.mastersys.fatura.domain.FaturaMatricula;
import dev.jose.mastersys.fatura.domain.enums.StatusFatura;
import dev.jose.mastersys.fatura.repository.FaturaMatriculaRepository;
import dev.jose.mastersys.matricula.domain.Matricula;
import dev.jose.mastersys.matricula.domain.MatriculaModalidade;
import dev.jose.mastersys.matricula.domain.enums.StatusMatricula;
import dev.jose.mastersys.matricula.repository.MatriculaModalidadeRepository;
import dev.jose.mastersys.matricula.repository.MatriculaRepository;
import dev.jose.mastersys.modalidade.domain.Modalidade;
import dev.jose.mastersys.modalidade.repository.ModalidadeRepository;
import dev.jose.mastersys.plano.domain.Plano;
import dev.jose.mastersys.plano.repository.PlanosRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomRepositoryQueriesTest {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ModalidadeRepository modalidadeRepository;

    @Autowired
    private PlanosRepository planosRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private MatriculaModalidadeRepository matriculaModalidadeRepository;

    @Autowired
    private FaturaMatriculaRepository faturaRepository;

    @Test
    void alunoRepositoryDeveBuscarEmailIgnorandoMaiusculas() {
        var aluno = salvarAluno("email-query-" + System.nanoTime() + "@example.com");

        assertTrue(alunoRepository.existsByEmailIgnoreCase(aluno.getEmail().toUpperCase()));
        assertFalse(alunoRepository.existsByEmailIgnoreCaseAndIdNot(aluno.getEmail(), aluno.getId()));
        assertTrue(alunoRepository.existsByEmailIgnoreCaseAndIdNot(aluno.getEmail(), -1L));
    }

    @Test
    void modalidadeRepositoryDeveBuscarNomeIgnorandoCaixaEAcentos() {
        var modalidade = new Modalidade();
        modalidade.setNome("Natação " + System.nanoTime());
        modalidade.setAtiva(true);
        modalidade = modalidadeRepository.saveAndFlush(modalidade);

        var nomeSemAcento = modalidade.getNome().replace("ã", "a").toUpperCase();
        var modalidadeId = modalidade.getId();
        assertTrue(modalidadeRepository.existsByNomeIgnoreCaseAndAcentos(nomeSemAcento));
        assertFalse(modalidadeRepository.existsByNomeIgnoreCaseAndAcentosAndIdNot(
                modalidade.getNome(), modalidadeId));
        assertTrue(modalidadeRepository.existsByNomeIgnoreCaseAndAcentosAndIdNot(
                modalidade.getNome(), -1L));
        assertTrue(modalidadeRepository.findByAtivaTrue().stream()
                .anyMatch(resultado -> resultado.getId().equals(modalidadeId)));
    }

    @Test
    void planoRepositoryDeveBuscarNomePorModalidadeIgnorandoCaixaEAcentos() {
        var modalidade = salvarModalidade("Modalidade query " + System.nanoTime(), true);
        var plano = new Plano();
        plano.setNome("Natação " + System.nanoTime());
        plano.setValor(new BigDecimal("120.00"));
        plano.setAtivo(true);
        plano.setModalidade(modalidade);
        plano = planosRepository.saveAndFlush(plano);

        var nomeSemAcento = plano.getNome().replace("ã", "a").toUpperCase();
        var planoId = plano.getId();
        var modalidadeId = modalidade.getId();
        assertTrue(planosRepository.existsByNomeIgnoreCaseAndAcentos(nomeSemAcento, modalidadeId));
        assertFalse(planosRepository.existsByNomeIgnoreCaseAndAcentosAndIdNot(
                plano.getNome(), modalidadeId, planoId));
        assertTrue(planosRepository.existsByNomeIgnoreCaseAndAcentosAndIdNot(
                plano.getNome(), modalidadeId, -1L));
        assertEquals(1, planosRepository.findAllByModalidadeId(modalidadeId).size());
    }

    @Test
    void matriculaRepositoryDeveFiltrarPeloStatusEAluno() {
        var aluno = salvarAluno("matricula-query-" + System.nanoTime() + "@example.com");
        var matricula = salvarMatricula(aluno, StatusMatricula.ATIVA, 10);

        assertTrue(matriculaRepository.existsByAlunoIdAndStatus(aluno.getId(), StatusMatricula.ATIVA));
        assertFalse(matriculaRepository.existsByAlunoIdAndStatus(aluno.getId(), StatusMatricula.CANCELADA));
        assertTrue(matriculaRepository.findAllByStatus(StatusMatricula.ATIVA).stream()
                .anyMatch(resultado -> resultado.getId().equals(matricula.getId())));
    }

    @Test
    void matriculaModalidadeRepositoryDeveBuscarVinculosAtivosDaMatricula() {
        var aluno = salvarAluno("vinculo-query-" + System.nanoTime() + "@example.com");
        var matricula = salvarMatricula(aluno, StatusMatricula.ATIVA, 10);
        var modalidade = salvarModalidade("Vinculo query " + System.nanoTime(), true);
        var plano = salvarPlano(modalidade, "Plano vinculo " + System.nanoTime());
        var vinculo = new MatriculaModalidade();
        vinculo.setMatricula(matricula);
        vinculo.setModalidade(modalidade);
        vinculo.setPlano(plano);
        var salvo = matriculaModalidadeRepository.saveAndFlush(vinculo);

        assertTrue(matriculaModalidadeRepository.existsByMatriculaIdAndModalidadeIdAndDataFimIsNull(
                matricula.getId(), modalidade.getId()));
        assertFalse(matriculaModalidadeRepository.existsByMatriculaIdAndModalidadeIdAndDataFimIsNull(
                matricula.getId(), -1L));
        assertEquals(1, matriculaModalidadeRepository.findAllByMatriculaId(matricula.getId()).size());

        salvo.setDataFim(LocalDate.now());
        matriculaModalidadeRepository.flush();
        assertFalse(matriculaModalidadeRepository.existsByMatriculaIdAndModalidadeIdAndDataFimIsNull(
                matricula.getId(), modalidade.getId()));
    }

    @Test
    void faturaRepositoryDeveConsultarExistenciaEStatusAntesDoVencimento() {
        var aluno = salvarAluno("fatura-query-" + System.nanoTime() + "@example.com");
        var matricula = salvarMatricula(aluno, StatusMatricula.ATIVA, 10);
        var fatura = new FaturaMatricula();
        fatura.setMatricula(matricula);
        fatura.setDataVencimento(LocalDate.now().minusDays(2));
        fatura.setValor(new BigDecimal("120.00"));
        fatura = faturaRepository.saveAndFlush(fatura);

        var faturaId = fatura.getId();
        assertTrue(faturaRepository.existsByMatriculaIdAndDataVencimento(
                matricula.getId(), fatura.getDataVencimento()));
        assertFalse(faturaRepository.existsByMatriculaIdAndDataVencimento(
                matricula.getId(), LocalDate.now()));
        assertTrue(faturaRepository.findAllByStatusFaturaAndDataVencimentoBefore(
                StatusFatura.ABERTA, LocalDate.now()).stream()
                .anyMatch(resultado -> resultado.getId().equals(faturaId)));
        assertFalse(faturaRepository.findAllByStatusFaturaAndDataVencimentoBefore(
                        StatusFatura.PAGA, LocalDate.now()).stream()
                .anyMatch(resultado -> resultado.getId().equals(faturaId)));
    }

    private Aluno salvarAluno(String email) {
        var aluno = new Aluno();
        aluno.setNome("Aluno de teste");
        aluno.setEmail(email);
        aluno.setSexo(Sexo.M);
        aluno.setCidade("Cruzeiro");
        return alunoRepository.saveAndFlush(aluno);
    }

    private Modalidade salvarModalidade(String nome, boolean ativa) {
        var modalidade = new Modalidade();
        modalidade.setNome(nome);
        modalidade.setAtiva(ativa);
        return modalidadeRepository.saveAndFlush(modalidade);
    }

    private Plano salvarPlano(Modalidade modalidade, String nome) {
        var plano = new Plano();
        plano.setNome(nome);
        plano.setAtivo(true);
        plano.setValor(new BigDecimal("120.00"));
        plano.setModalidade(modalidade);
        return planosRepository.saveAndFlush(plano);
    }

    private Matricula salvarMatricula(Aluno aluno, StatusMatricula status, int diaVencimento) {
        var matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setStatus(status);
        matricula.setDiaVencimento(diaVencimento);
        return matriculaRepository.saveAndFlush(matricula);
    }
}

package dev.jose.mastersys.controller;

import dev.jose.mastersys.aluno.dto.AlunoResponse;
import dev.jose.mastersys.aluno.service.AlunoService;
import dev.jose.mastersys.exception.ExclusaoComRelacionamentosException;
import dev.jose.mastersys.exception.GlobalExceptionHandler;
import dev.jose.mastersys.fatura.domain.enums.StatusFatura;
import dev.jose.mastersys.fatura.dto.FaturaMatriculaResponse;
import dev.jose.mastersys.fatura.service.FaturaMatriculaService;
import dev.jose.mastersys.matricula.domain.enums.StatusMatricula;
import dev.jose.mastersys.matricula.dto.MatriculaModalidadeResponse;
import dev.jose.mastersys.matricula.dto.MatriculaResponse;
import dev.jose.mastersys.matricula.service.MatriculaModalidadeService;
import dev.jose.mastersys.matricula.service.MatriculaService;
import dev.jose.mastersys.modalidade.dto.ModalidadeResponse;
import dev.jose.mastersys.modalidade.service.ModalidadeService;
import dev.jose.mastersys.plano.dto.PlanoResponse;
import dev.jose.mastersys.plano.service.PlanoService;
import dev.jose.mastersys.relatorio.service.RelatorioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {
        dev.jose.mastersys.aluno.controller.AlunoController.class,
        dev.jose.mastersys.plano.controller.PlanoController.class,
        dev.jose.mastersys.modalidade.controller.ModalidadeController.class,
        dev.jose.mastersys.matricula.controller.MatriculaController.class,
        dev.jose.mastersys.matricula.controller.MatriculaModalidadeController.class,
        dev.jose.mastersys.fatura.controller.FaturaMatriculaController.class,
        dev.jose.mastersys.relatorio.controller.RelatorioController.class
})
@Import(GlobalExceptionHandler.class)
class HttpControllersIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlunoService alunoService;

    @MockitoBean
    private PlanoService planoService;

    @MockitoBean
    private ModalidadeService modalidadeService;

    @MockitoBean
    private MatriculaService matriculaService;

    @MockitoBean
    private MatriculaModalidadeService matriculaModalidadeService;

    @MockitoBean
    private FaturaMatriculaService faturaMatriculaService;

    @MockitoBean
    private RelatorioService relatorioService;

    @Test
    void deveCriarAlunoComRespostaJsonECriarStatus() throws Exception {
        when(alunoService.cadastrar(any())).thenReturn(alunoResponse());

        mockMvc.perform(post("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Ana Silva",
                                  "dataNascimento": "1995-05-10",
                                  "sexo": "F",
                                  "telefone": "123456",
                                  "celular": "11999999999",
                                  "email": "ana@example.com",
                                  "observacao": "Teste",
                                  "endereco": "Rua Um",
                                  "numero": "10",
                                  "complemento": "Casa",
                                  "bairro": "Centro",
                                  "cidade": "Cruzeiro",
                                  "estado": "SP",
                                  "cep": "12700000"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Ana Silva"))
                .andExpect(jsonPath("$.email").value("ana@example.com"));

        verify(alunoService).cadastrar(any());
    }

    @Test
    void deveRetornarBadRequestQuandoAlunoForInvalido() throws Exception {
        mockMvc.perform(post("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "",
                                  "sexo": null,
                                  "email": "email-invalido"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Erro de validação"))
                .andExpect(jsonPath("$.mensagens").isArray());

        verifyNoInteractions(alunoService);
    }

    @Test
    void deveMapearFalhaDeExclusaoParaConflitoHttp() throws Exception {
        doThrow(new ExclusaoComRelacionamentosException("o aluno", null))
                .when(alunoService).excluir(1L);

        mockMvc.perform(delete("/alunos/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro").value("Exclusão não permitida"));

        verify(alunoService).excluir(1L);
    }

    @Test
    void deveExporRotasHttpDePlanoEModalidade() throws Exception {
        when(planoService.cadastrarPlano(any())).thenReturn(planoResponse());
        when(modalidadeService.cadastrarModalidade(any())).thenReturn(modalidadeResponse());
        when(modalidadeService.listarModalidadesAtivas()).thenReturn(List.of(modalidadeResponse()));

        mockMvc.perform(post("/planos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Mensal","valor":130.00,"modalidadeId":2}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Mensal"))
                .andExpect(jsonPath("$.valor").value(130.00));

        mockMvc.perform(post("/modalidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Natação"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Natação"))
                .andExpect(jsonPath("$.ativa").value(true));

        mockMvc.perform(get("/modalidades/disponiveis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    void deveExporRotasHttpDeMatriculaEVinculoDeModalidade() throws Exception {
        when(matriculaService.criarMatricula(any())).thenReturn(matriculaResponse());
        when(matriculaService.buscarPorId(1L)).thenReturn(matriculaResponse());
        when(matriculaModalidadeService.criarMatriculaModalidade(any())).thenReturn(matriculaModalidadeResponse());

        mockMvc.perform(post("/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"alunoId":3,"diaVencimento":15}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ATIVA"));

        mockMvc.perform(get("/matriculas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alunoId").value(3));

        mockMvc.perform(post("/matriculas/modalidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"matriculaId":1,"planoId":4}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.modalidadeId").value(5))
                .andExpect(jsonPath("$.planoId").value(4));
    }

    @Test
    void deveRejeitarDiaDeVencimentoInvalidoNoHttp() throws Exception {
        mockMvc.perform(post("/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"alunoId":3,"diaVencimento":40}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagens").isArray());

        verifyNoInteractions(matriculaService);
    }

    @Test
    void deveExporRotasHttpDeFaturas() throws Exception {
        when(faturaMatriculaService.criarFatura(any())).thenReturn(faturaResponse());
        when(faturaMatriculaService.pagarFatura(6L)).thenReturn(faturaResponsePaga());
        when(faturaMatriculaService.listarFaturas(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(post("/faturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"matriculaId":1}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ABERTA"))
                .andExpect(jsonPath("$.valor").value(130.00));

        mockMvc.perform(patch("/faturas/6/pagar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAGA"));

        mockMvc.perform(get("/faturas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(faturaMatriculaService).criarFatura(any());
        verify(faturaMatriculaService).pagarFatura(6L);
        verify(faturaMatriculaService).listarFaturas(any(), any());
    }

    @Test
    void deveVincularParametrosDeRelatorioNaQueryString() throws Exception {
        when(relatorioService.alunosPorCidade("Cruzeiro")).thenReturn(List.of());
        when(relatorioService.faturaPorStatus(StatusFatura.PAGA)).thenReturn(List.of());

        mockMvc.perform(get("/relatorios/alunos-por-cidade").param("cidade", "Cruzeiro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/relatorios/faturas-por-status").param("status", "PAGA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/relatorios/faturas-por-status").param("status", "DESCONHECIDO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Parâmetro inválido"));

        verify(relatorioService).alunosPorCidade("Cruzeiro");
        verify(relatorioService).faturaPorStatus(StatusFatura.PAGA);
    }

    private AlunoResponse alunoResponse() {
        return new AlunoResponse(1L, "Ana Silva", LocalDate.of(1995, 5, 10),
                dev.jose.mastersys.aluno.domain.enums.Sexo.F, "11999999999",
                "ana@example.com", "Cruzeiro", "SP", "Teste",
                LocalDateTime.now(), null);
    }

    private PlanoResponse planoResponse() {
        return new PlanoResponse(4L, "Mensal", true, 2L, "Academia", new BigDecimal("130.00"));
    }

    private ModalidadeResponse modalidadeResponse() {
        return new ModalidadeResponse(2L, "Natação", true);
    }

    private MatriculaResponse matriculaResponse() {
        return new MatriculaResponse(1L, LocalDate.now(), 15, null, StatusMatricula.ATIVA, 3L);
    }

    private MatriculaModalidadeResponse matriculaModalidadeResponse() {
        return new MatriculaModalidadeResponse(7L, LocalDate.now(), null, 1L, 5L, 4L);
    }

    private FaturaMatriculaResponse faturaResponse() {
        return new FaturaMatriculaResponse(6L, LocalDate.now().plusDays(15),
                new BigDecimal("130.00"), null, null, StatusFatura.ABERTA, 1L);
    }

    private FaturaMatriculaResponse faturaResponsePaga() {
        return new FaturaMatriculaResponse(6L, LocalDate.now().plusDays(15),
                new BigDecimal("130.00"), LocalDateTime.now(), null, StatusFatura.PAGA, 1L);
    }
}

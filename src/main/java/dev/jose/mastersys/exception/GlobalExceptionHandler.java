package dev.jose.mastersys.exception;

import dev.jose.mastersys.aluno.exception.AlunoNaoEncontradoException;
import dev.jose.mastersys.fatura.exception.FaturaDuplicadaException;
import dev.jose.mastersys.fatura.exception.FaturaNaoEncontradaException;
import dev.jose.mastersys.fatura.exception.FaturaSemModalidadeAtivaException;
import dev.jose.mastersys.fatura.exception.StatusFaturaInvalidoException;
import dev.jose.mastersys.matricula.exception.DiaVencimentoInvalidoException;
import dev.jose.mastersys.matricula.exception.MatriculaModalidadeNaoEncontradaException;
import dev.jose.mastersys.matricula.exception.MatriculaNaoEncontradaException;
import dev.jose.mastersys.matricula.exception.StatusMatriculaInvalidoException;
import dev.jose.mastersys.modalidade.exception.ModalidadeNaoEncontradaException;
import dev.jose.mastersys.plano.exception.PlanoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarErroValidacao(MethodArgumentNotValidException ex) {
        List<String> mensagens = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();

       return criarResposta(HttpStatus.BAD_REQUEST, "Erro de validação", mensagens);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> tratarTipoDeParametroInvalido(MethodArgumentTypeMismatchException ex) {
        String parametro = ex.getName();
        String valor = String.valueOf(ex.getValue());
        return criarResposta(HttpStatus.BAD_REQUEST, "Parâmetro inválido",
                List.of("O valor '" + valor + "' não é válido para o parâmetro '" + parametro + "'."));
    }

    //Tratamentos de busca especificos
    @ExceptionHandler(AlunoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarAlunoNaoEncontrado(AlunoNaoEncontradoException ex){
        return criarResposta(HttpStatus.NOT_FOUND,"Recurso não encontrado", List.of(ex.getMessage()));
    }

    @ExceptionHandler(ModalidadeNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> tratarModalidadeNaoEncontrada(ModalidadeNaoEncontradaException ex) {
        return criarResposta(HttpStatus.NOT_FOUND, "Recurso não encontrado", List.of(ex.getMessage())
        );
    }

    @ExceptionHandler(PlanoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarPlanoNaoEncontrado(PlanoNaoEncontradoException ex){
        return criarResposta(HttpStatus.NOT_FOUND, "Recurso não encontrado", List.of(ex.getMessage()));
    }

    @ExceptionHandler(FaturaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> tratarFaturaNaoEncontrada(FaturaNaoEncontradaException ex) {
        return criarResposta(HttpStatus.NOT_FOUND, "Recurso não encontrado", List.of(ex.getMessage()));
    }

    @ExceptionHandler(FaturaDuplicadaException.class)
    public ResponseEntity<ErroResponse> tratarFaturaDuplicada(FaturaDuplicadaException ex) {
        return criarResposta(HttpStatus.CONFLICT, "Fatura já cadastrada", List.of(ex.getMessage()));
    }

    @ExceptionHandler(FaturaSemModalidadeAtivaException.class)
    public ResponseEntity<ErroResponse> tratarFaturaSemModalidadeAtiva(
            FaturaSemModalidadeAtivaException ex) {
        return criarResposta(HttpStatus.CONFLICT, "Matrícula sem modalidade ativa", List.of(ex.getMessage()));
    }

    @ExceptionHandler(MatriculaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> tratarMatriculaNaoEncontrada(MatriculaNaoEncontradaException ex) {
        return criarResposta(HttpStatus.NOT_FOUND, "Recurso não encontrado", List.of(ex.getMessage()));
    }

    @ExceptionHandler(StatusMatriculaInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarStatusInvalido(StatusMatriculaInvalidoException ex) {

        return criarResposta(HttpStatus.BAD_REQUEST, "Status da matrícula inválido", List.of(ex.getMessage()));
    }

    @ExceptionHandler(DiaVencimentoInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarDiaVencimentoInvalido(DiaVencimentoInvalidoException ex) {
        return criarResposta(HttpStatus.BAD_REQUEST, "Dia de vencimento inválido", List.of(ex.getMessage()));
    }
    @ExceptionHandler(StatusFaturaInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarStatusFaturaInvalido(StatusFaturaInvalidoException ex) {
        return criarResposta(HttpStatus.CONFLICT, "Conflito de status da fatura", List.of(ex.getMessage()));
    }

    @ExceptionHandler(MatriculaModalidadeNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> tratarMatriculaModalidadeNaoEncontrada(
            MatriculaModalidadeNaoEncontradaException ex) {
        return criarResposta(HttpStatus.NOT_FOUND,  "Recurso não encontrado", List.of(ex.getMessage()));
    }


    //Tratamentos de genericos
    @ExceptionHandler(RecursoJaCadastradoException.class)
    public ResponseEntity<ErroResponse> tratarRecursoJaCadastrado(
            RecursoJaCadastradoException ex) {

        return criarResposta(HttpStatus.CONFLICT, "Conflito", List.of(ex.getMessage()));
    }

    @ExceptionHandler(RecursoJaAtivoException.class)
    public ResponseEntity<ErroResponse> tratarRecursoJaAtiva(RecursoJaAtivoException ex) {
        return criarResposta(HttpStatus.CONFLICT, "Conflito", List.of(ex.getMessage())
        );
    }

    @ExceptionHandler(RecursoJaInativoException.class)
    public ResponseEntity<ErroResponse> tratarRecursoJaInativa(RecursoJaInativoException ex) {
        return criarResposta(HttpStatus.CONFLICT, "Conflito", List.of(ex.getMessage())
        );
    }

    @ExceptionHandler(ExclusaoComRelacionamentosException.class)
    public ResponseEntity<ErroResponse> tratarExclusaoComRelacionamentos(
            ExclusaoComRelacionamentosException ex) {
        return criarResposta(HttpStatus.CONFLICT, "Exclusão não permitida", List.of(ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> tratarViolacaoDeIntegridade(DataIntegrityViolationException ex) {
        return criarResposta(HttpStatus.CONFLICT, "Conflito com os dados existentes",
                List.of("A operação não pode ser concluída porque há registros relacionados ou outra restrição de integridade."));
    }


    //Tratamento geral(melhorar)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroGenerico(Exception ex, HttpServletRequest request) {

        log.error("Erro interno | método={} | URI={}", request.getMethod(), request.getRequestURI(), ex);

        return criarResposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor",
                List.of("Ocorreu um erro inesperado.")
        );
    }

    private ResponseEntity<ErroResponse> criarResposta(
            HttpStatus status,
            String titulo,
            List<String> mensagens) {

        ErroResponse response = new ErroResponse(
                LocalDateTime.now(),
                status.value(),
                titulo,
                mensagens
        );

        return ResponseEntity.status(status).body(response);
    }
}

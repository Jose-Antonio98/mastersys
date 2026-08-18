package dev.jose.mastersys.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarErroValidacao(MethodArgumentNotValidException ex) {
        List<String> mensagens = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();

       return criarResposta(HttpStatus.BAD_REQUEST, "Erro de validação", mensagens);
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
        return criarResposta(HttpStatus.BAD_REQUEST, "Dia de vencimento invalido", List.of(ex.getMessage()));
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


    //Tratamento geral(melhorar)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroGenerico(Exception ex) {

        return criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor",
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

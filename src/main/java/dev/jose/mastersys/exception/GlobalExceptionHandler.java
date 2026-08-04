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

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErroResponse> tratarEmailJaCadastrado(
            EmailJaCadastradoException ex) {

        return criarResposta(HttpStatus.CONFLICT, "Conflito", List.of(ex.getMessage()));
    }

    @ExceptionHandler(AlunoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarAlunoNaoEncontrado(
            AlunoNaoEncontradoException ex){

        return criarResposta(HttpStatus.NOT_FOUND,"Recurso não encontrado", List.of(ex.getMessage()));
    }

    @ExceptionHandler(ModalidadeJaAtiva.class)
    public ResponseEntity<ErroResponse> tratarModalidadeJaAtiva(ModalidadeJaAtiva ex) {
        return criarResposta(HttpStatus.CONFLICT, "Conflito", List.of(ex.getMessage())
        );
    }

    @ExceptionHandler(ModalidadeJaInativa.class)
    public ResponseEntity<ErroResponse> tratarModalidadeJaInativa(ModalidadeJaInativa ex) {
        return criarResposta(HttpStatus.CONFLICT, "Conflito", List.of(ex.getMessage())
        );
    }

    @ExceptionHandler(ModalidadeJaCadastradaException.class)
    public ResponseEntity<ErroResponse> tratarModalidadeJaCadastrada(ModalidadeJaCadastradaException ex) {
        return criarResposta(HttpStatus.CONFLICT, "Conflito", List.of(ex.getMessage())
        );
    }

    @ExceptionHandler(ModalidadeNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> tratarModalidadeNaoEncontrada(ModalidadeNaoEncontradaException ex) {
        return criarResposta(HttpStatus.NOT_FOUND, "Recurso não encontrado", List.of(ex.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroGenerico() {

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

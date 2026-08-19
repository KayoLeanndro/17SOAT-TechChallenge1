package com.kap.mechanics_api.infra;

import com.kap.mechanics_api.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VeiculoNaoEncontradoException.class)
    public ProblemDetail lancarExcecaoVeiculoNaoEncontrado(VeiculoNaoEncontradoException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Veículo não encontrado");
        return problemDetail;
    }

    @ExceptionHandler(ServicoNaoEncontradoException.class)
    public ProblemDetail lancarExcecaoPecaNaoEncontrada(ServicoNaoEncontradoException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Servico não encontrado");
        return problemDetail;
    }

    @ExceptionHandler(ItemEstoqueNaoEncontradoException.class)
    public ProblemDetail lancarExcecaoItemEstoqueNaoEncontrado(ItemEstoqueNaoEncontradoException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Item de estoque não encontrado");
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Erro de validação");
        Map<String, String> erros = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));
        problemDetail.setProperty("erros", erros);
        return problemDetail;
    }

    @ExceptionHandler(NenhumCampoInformadoException.class)
    public ProblemDetail handleNenhumCampoInformado(NenhumCampoInformadoException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Nenhum campo informado");
        problemDetail.setProperty("camposDisponiveis", ex.getCamposDisponiveis());
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleJsonInvalido(
            HttpMessageNotReadableException ex) {

        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        "O corpo da requisição contém um campo com valor inválido."
                );

        problemDetail.setTitle("Requisição inválida");

        return problemDetail;
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ProblemDetail lancarExcecaoUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Usuario não encontrado");
        return problemDetail;
    }

    @ExceptionHandler(OrcamentoNaoEncontradoException.class)
    public ProblemDetail lancarExcecaoOrcamentoNaoEncontrado(OrcamentoNaoEncontradoException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Orçamento não encontrado");
        return problemDetail;
    }

//    @ExceptionHandler(OrdemServicoNaoEncontradaException.class)
//    public ProblemDetail lancarExcecaoOrdemServicoNaoEncontrada(OrdemServicoNaoEncontradaException ex) {
//        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
//        problemDetail.setTitle("Ordem de serviço não encontrada");
//        return problemDetail;
//    }
//
//    @ExceptionHandler(StatusOrdemServicoNaoEncontradoException.class)
//    public ProblemDetail lancarExcecaoStatusOrdemServicoNaoEncontrado(StatusOrdemServicoNaoEncontradoException ex) {
//        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
//        problemDetail.setTitle("Status da ordem de serviço não encontrado");
//        return problemDetail;
//    }
}

package com.dio.springai.controller;

import com.dio.springai.model.TransacaoRequest;
import com.dio.springai.model.TransacaoResponse;
import com.dio.springai.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
@Tag(name = "Transações Financeiras", description = "Endpoints REST padrão para listagem e cadastro manual de movimentações")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping
    @Operation(summary = "Listar todas as transações", description = "Retorna o histórico de todas as transações ordenadas pela data mais recente.")
    public ResponseEntity<List<TransacaoResponse>> listarTodas() {
        return ResponseEntity.ok(transacaoService.listarTodas());
    }

    @PostMapping
    @Operation(summary = "Cadastrar transação manualmente", description = "Permite o registro tradicional de uma receita ou despesa via JSON.")
    public ResponseEntity<TransacaoResponse> cadastrar(@Valid @RequestBody TransacaoRequest request) {
        TransacaoResponse response = transacaoService.registrarTransacao(
                request.descricao(),
                request.valor(),
                request.tipo(),
                request.categoria()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

package com.dio.springai.service;

import com.dio.springai.model.*;
import com.dio.springai.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransacaoServiceTest {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private TransacaoRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Deve registrar receita e despesa com sucesso e calcular o resumo financeiro")
    void deveRegistrarTransacoesECalcularResumo() {
        // 1. Cadastra uma receita (Salário)
        TransacaoResponse receita = transacaoService.registrarTransacao(
                "Salário Mensal",
                new BigDecimal("5000.00"),
                TipoTransacao.RECEITA,
                CategoriaTransacao.SALARIO
        );

        assertNotNull(receita.id());
        assertEquals("Salário Mensal", receita.descricao());
        assertEquals(new BigDecimal("5000.00"), receita.valor());
        assertEquals(TipoTransacao.RECEITA, receita.tipo());

        // 2. Cadastra despesas
        transacaoService.registrarTransacao(
                "Supermercado Semanal",
                new BigDecimal("600.00"),
                TipoTransacao.DESPESA,
                CategoriaTransacao.ALIMENTACAO
        );

        transacaoService.registrarTransacao(
                "Uber para o trabalho",
                new BigDecimal("150.00"),
                TipoTransacao.DESPESA,
                CategoriaTransacao.TRANSPORTE
        );

        // 3. Valida listagem
        List<TransacaoResponse> todas = transacaoService.listarTodas();
        assertEquals(3, todas.size());

        // 4. Valida resumo financeiro
        ResumoFinanceiroResponse resumo = transacaoService.obterResumoFinanceiro();
        assertEquals(new BigDecimal("5000.00"), resumo.totalReceitas());
        assertEquals(new BigDecimal("750.00"), resumo.totalDespesas());
        assertEquals(new BigDecimal("4250.00"), resumo.saldoAtual());
        assertEquals(3, resumo.totalTransacoes());
        assertEquals("SUPERAVIT", resumo.statusOrcamento());
        assertTrue(resumo.despesasPorCategoria().containsKey(CategoriaTransacao.ALIMENTACAO));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar registrar transação com valor zero ou negativo")
    void deveLancarExcecaoValorInvalido() {
        assertThrows(IllegalArgumentException.class, () ->
                transacaoService.registrarTransacao("Teste", BigDecimal.ZERO, TipoTransacao.DESPESA, CategoriaTransacao.OUTROS)
        );

        assertThrows(IllegalArgumentException.class, () ->
                transacaoService.registrarTransacao("Teste Negativo", new BigDecimal("-10.00"), TipoTransacao.DESPESA, CategoriaTransacao.OUTROS)
        );
    }
}

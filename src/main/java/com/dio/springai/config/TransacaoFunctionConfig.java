package com.dio.springai.config;

import com.dio.springai.model.CategoriaTransacao;
import com.dio.springai.model.ResumoFinanceiroResponse;
import com.dio.springai.model.TipoTransacao;
import com.dio.springai.model.TransacaoResponse;
import com.dio.springai.service.TransacaoService;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Padrão Spring AI Function Calling / Tool Calling:
 * Declara funções como Beans Spring do tipo java.util.function.Function.
 * O Spring AI mapeia essas funções para o esquema JSON da OpenAI, permitindo que a IA
 * decida acioná-las durante o fluxo de raciocínio.
 */
@Configuration
public class TransacaoFunctionConfig {

    private static final Logger log = LoggerFactory.getLogger(TransacaoFunctionConfig.class);

    private final TransacaoService transacaoService;

    public TransacaoFunctionConfig(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    public record RegistrarTransacaoRequest(
            @JsonPropertyDescription("Descrição sucinta do gasto ou ganho, ex: Almoço, Uber, Salário")
            String descricao,

            @JsonPropertyDescription("Valor monetário em Reais, positivo, ex: 45.90")
            BigDecimal valor,

            @JsonPropertyDescription("Tipo da transação: RECEITA ou DESPESA")
            TipoTransacao tipo,

            @JsonPropertyDescription("Categoria: ALIMENTACAO, TRANSPORTE, MORADIA, SAUDE, EDUCACAO, LAZER, SALARIO, INVESTIMENTO ou OUTROS")
            CategoriaTransacao categoria
    ) {}

    public record ConsultarResumoRequest(
            @JsonPropertyDescription("Parâmetro opcional de consulta")
            String filtro
    ) {}

    public record ConsultarCategoriaRequest(
            @JsonPropertyDescription("Categoria para consulta de gastos")
            CategoriaTransacao categoria
    ) {}

    @Bean
    @Description("Registra uma nova movimentação financeira (receita ou despesa) no orçamento do usuário.")
    public Function<RegistrarTransacaoRequest, String> registrarTransacaoFunction() {
        return request -> {
            log.info("Function Calling [registrarTransacaoFunction] acionada pela IA: {}", request);
            TransacaoResponse response = transacaoService.registrarTransacao(
                    request.descricao(),
                    request.valor(),
                    request.tipo(),
                    request.categoria()
            );
            return String.format("SUCESSO: Transação #%d cadastrada. Descrição: '%s', Valor: R$ %s, Tipo: %s, Categoria: %s.",
                    response.id(), response.descricao(), response.valor(), response.tipo(), response.categoria());
        };
    }

    @Bean
    @Description("Consulta o saldo atual, o total de receitas, o total de despesas e o resumo geral do orçamento.")
    public Function<ConsultarResumoRequest, String> consultarResumoFunction() {
        return request -> {
            log.info("Function Calling [consultarResumoFunction] acionada pela IA");
            ResumoFinanceiroResponse resumo = transacaoService.obterResumoFinanceiro();
            return String.format("RESUMO: Total Receitas: R$ %s | Total Despesas: R$ %s | Saldo Atual: R$ %s | Status: %s | Alerta: %s",
                    resumo.totalReceitas(), resumo.totalDespesas(), resumo.saldoAtual(), resumo.statusOrcamento(), resumo.alertaFinanceiro());
        };
    }

    @Bean
    @Description("Consulta o total de gastos acumulados em uma categoria específica como ALIMENTACAO, TRANSPORTE, LAZER, etc.")
    public Function<ConsultarCategoriaRequest, String> consultarGastosPorCategoriaFunction() {
        return request -> {
            log.info("Function Calling [consultarGastosPorCategoriaFunction] acionada pela IA: {}", request.categoria());
            return transacaoService.consultarGastosPorCategoria(request.categoria());
        };
    }
}

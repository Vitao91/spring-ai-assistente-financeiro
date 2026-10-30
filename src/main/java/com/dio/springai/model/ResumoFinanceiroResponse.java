package com.dio.springai.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Map;

@Schema(description = "Resumo consolidado da saúde financeira e orçamento")
public record ResumoFinanceiroResponse(
    @Schema(description = "Soma total de receitas registradas", example = "5000.00")
    BigDecimal totalReceitas,

    @Schema(description = "Soma total de despesas registradas", example = "2150.50")
    BigDecimal totalDespesas,

    @Schema(description = "Saldo líquido atual (Receitas - Despesas)", example = "2849.50")
    BigDecimal saldoAtual,

    @Schema(description = "Quantidade total de transações cadastradas", example = "14")
    long totalTransacoes,

    @Schema(description = "Despesas agrupadas por categoria")
    Map<CategoriaTransacao, BigDecimal> despesasPorCategoria,

    @Schema(description = "Status do orçamento (SUPERAVIT, EQUILIBRADO ou DEFICIT)", example = "SUPERAVIT")
    String statusOrcamento,

    @Schema(description = "Dica ou alerta financeiro gerado para o usuário")
    String alertaFinanceiro
) {}

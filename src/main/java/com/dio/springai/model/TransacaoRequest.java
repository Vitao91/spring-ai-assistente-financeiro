package com.dio.springai.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "Requisição para cadastrar transação manualmente")
public record TransacaoRequest(
    @NotBlank(message = "A descrição é obrigatória")
    @Schema(description = "Descrição da transação", example = "Almoço de domingo")
    String descricao,

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    @Schema(description = "Valor da movimentação", example = "45.90")
    BigDecimal valor,

    @NotNull(message = "O tipo de transação é obrigatório (RECEITA ou DESPESA)")
    @Schema(description = "Tipo da transação", example = "DESPESA")
    TipoTransacao tipo,

    @NotNull(message = "A categoria é obrigatória")
    @Schema(description = "Categoria da transação", example = "ALIMENTACAO")
    CategoriaTransacao categoria
) {}

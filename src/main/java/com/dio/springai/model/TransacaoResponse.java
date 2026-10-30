package com.dio.springai.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Detalhes de uma transação cadastrada")
public record TransacaoResponse(
    @Schema(description = "ID único", example = "1")
    Long id,

    @Schema(description = "Descrição", example = "Almoço de domingo")
    String descricao,

    @Schema(description = "Valor", example = "45.90")
    BigDecimal valor,

    @Schema(description = "Tipo", example = "DESPESA")
    TipoTransacao tipo,

    @Schema(description = "Categoria", example = "ALIMENTACAO")
    CategoriaTransacao categoria,

    @Schema(description = "Data e hora do registro")
    LocalDateTime dataHora
) {
    public static TransacaoResponse fromEntity(Transacao transacao) {
        return new TransacaoResponse(
            transacao.getId(),
            transacao.getDescricao(),
            transacao.getValor(),
            transacao.getTipo(),
            transacao.getCategoria(),
            transacao.getDataHora()
        );
    }
}

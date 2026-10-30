package com.dio.springai.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipo de movimentação financeira")
public enum TipoTransacao {
    RECEITA,
    DESPESA
}

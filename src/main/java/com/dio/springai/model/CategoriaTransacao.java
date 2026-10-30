package com.dio.springai.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Categorias de receitas e despesas")
public enum CategoriaTransacao {
    ALIMENTACAO,
    TRANSPORTE,
    MORADIA,
    SAUDE,
    EDUCACAO,
    LAZER,
    SALARIO,
    INVESTIMENTO,
    OUTROS
}

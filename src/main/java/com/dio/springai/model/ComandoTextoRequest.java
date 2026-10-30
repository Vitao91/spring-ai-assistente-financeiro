package com.dio.springai.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Comando em texto enviado ao assistente de IA")
public record ComandoTextoRequest(
    @NotBlank(message = "O comando não pode estar vazio")
    @Schema(description = "Mensagem ou comando para a IA", example = "Gastei 55 reais no mercado com compras de casa hoje")
    String comando
) {}

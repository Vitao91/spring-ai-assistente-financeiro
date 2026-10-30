package com.dio.springai.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Resposta estruturada do assistente inteligente")
public record RespostaAssistenteResponse(
    @Schema(description = "Texto transcrito a partir do áudio (se a entrada foi voz)", example = "Gastei 55 reais no mercado com compras de casa hoje")
    String textoTranscrito,

    @Schema(description = "Resposta gerada pela IA após raciocínio e execução de funções", example = "Com certeza! Registrei uma despesa de R$ 55,00 na categoria ALIMENTACAO.")
    String respostaAssistente,

    @Schema(description = "Ação executada via Tool Calling pela IA", example = "REGISTRAR_TRANSACAO")
    String acaoExecutada,

    @Schema(description = "Se foi solicitada resposta em voz, o áudio codificado em Base64")
    String audioBase64,

    @Schema(description = "Data e hora do processamento")
    LocalDateTime dataProcessamento
) {}

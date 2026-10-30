package com.dio.springai.controller;

import com.dio.springai.model.ComandoTextoRequest;
import com.dio.springai.model.RespostaAssistenteResponse;
import com.dio.springai.model.ResumoFinanceiroResponse;
import com.dio.springai.service.TransacaoService;
import com.dio.springai.service.ai.AssistenteFinanceiroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/assistente")
@Tag(name = "Assistente Inteligente", description = "Endpoints de interação por texto e voz usando Spring AI e Tool Calling")
public class AssistenteController {

    private final AssistenteFinanceiroService assistenteService;
    private final TransacaoService transacaoService;

    public AssistenteController(AssistenteFinanceiroService assistenteService,
                                TransacaoService transacaoService) {
        this.assistenteService = assistenteService;
        this.transacaoService = transacaoService;
    }

    @PostMapping("/comando-texto")
    @Operation(
        summary = "Enviar comando financeiro em texto",
        description = "Recebe uma mensagem em linguagem natural (ex: 'Gastei 45 no mercado hoje'), interpreta com IA, aciona a Tool correspondente e devolve a resposta."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comando processado com sucesso",
                content = @Content(schema = @Schema(implementation = RespostaAssistenteResponse.class))),
        @ApiResponse(responseCode = "400", description = "Comando inválido ou em branco")
    })
    public ResponseEntity<RespostaAssistenteResponse> enviarComandoTexto(@Valid @RequestBody ComandoTextoRequest request) {
        RespostaAssistenteResponse response = assistenteService.processarComandoTexto(request.comando());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/comando-voz", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Enviar comando financeiro por áudio/voz",
        description = "Recebe um arquivo de áudio (.mp3, .wav, .m4a), transcreve para texto via Whisper, compreende com IA, executa funções no sistema e opcionalmente gera resposta em áudio (TTS)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Áudio processado e ação executada com sucesso",
                content = @Content(schema = @Schema(implementation = RespostaAssistenteResponse.class))),
        @ApiResponse(responseCode = "400", description = "Arquivo de áudio inválido")
    })
    public ResponseEntity<RespostaAssistenteResponse> enviarComandoVoz(
            @Parameter(description = "Arquivo de áudio contendo o comando de voz", required = true)
            @RequestParam("audio") MultipartFile audio,
            @Parameter(description = "Se verdadeiro, sintetiza a resposta da IA em áudio (Base64) além do texto")
            @RequestParam(value = "respostaEmAudio", defaultValue = "false") boolean respostaEmAudio) {

        RespostaAssistenteResponse response = assistenteService.processarComandoVoz(audio, respostaEmAudio);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resumo")
    @Operation(
        summary = "Consultar resumo financeiro e orçamento",
        description = "Retorna o total de receitas, despesas, saldo líquido atual, categorias e análise de saúde financeira."
    )
    public ResponseEntity<ResumoFinanceiroResponse> consultarResumo() {
        ResumoFinanceiroResponse resumo = transacaoService.obterResumoFinanceiro();
        return ResponseEntity.ok(resumo);
    }
}

package com.dio.springai.service.ai;

import com.dio.springai.model.*;
import com.dio.springai.service.TransacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AssistenteFinanceiroService {

    private static final Logger log = LoggerFactory.getLogger(AssistenteFinanceiroService.class);

    private final ChatClient chatClient;
    private final AudioService audioService;
    private final TransacaoService transacaoService;

    @Value("${spring.ai.openai.api-key:demo-key}")
    private String apiKey;

    public AssistenteFinanceiroService(ChatClient chatClient,
                                       AudioService audioService,
                                       TransacaoService transacaoService) {
        this.chatClient = chatClient;
        this.audioService = audioService;
        this.transacaoService = transacaoService;
    }

    public RespostaAssistenteResponse processarComandoTexto(String comando) {
        if (comando == null || comando.trim().isEmpty()) {
            throw new IllegalArgumentException("O comando não pode estar vazio.");
        }

        log.info("Processando comando de texto recebido: \"{}\"", comando);

        String respostaTexto;
        String acaoExecutada = "CHAMADA_IA";

        if (isDemoKey()) {
            log.info("Chave OpenAI configurada como DEMO. Utilizando motor de interpretação de regras local.");
            respostaTexto = processarFallbackLocal(comando);
            acaoExecutada = "FALLBACK_LOCAL_SIMULADO";
        } else {
            try {
                // Execução real via ChatClient com Function Calling
                respostaTexto = chatClient.prompt()
                        .user(comando)
                        .call()
                        .content();
                log.info("Resposta do ChatClient gerada com sucesso.");
            } catch (Exception ex) {
                log.error("Falha ao comunicar com a OpenAI: {}. Executando fallback inteligente.", ex.getMessage());
                respostaTexto = processarFallbackLocal(comando);
                acaoExecutada = "FALLBACK_ERRO_API";
            }
        }

        return new RespostaAssistenteResponse(
                null,
                respostaTexto,
                acaoExecutada,
                null,
                LocalDateTime.now()
        );
    }

    public RespostaAssistenteResponse processarComandoVoz(MultipartFile audio, boolean gerarAudioResposta) {
        log.info("Recebida requisição de comando de voz. Arquivo: {}, Tamanho: {} bytes",
                audio.getOriginalFilename(), audio.getSize());

        // 1. Transcrição de Áudio (Whisper)
        String textoTranscrito = audioService.transcreverAudio(audio);

        // 2. Processamento da Intenção via IA + Execução de Funções Reais (Function Calling)
        RespostaAssistenteResponse respostaTexto = processarComandoTexto(textoTranscrito);

        // 3. Síntese de Voz opcional (TTS)
        String audioBase64 = null;
        if (gerarAudioResposta) {
            byte[] bytesAudio = audioService.sintetizarVoz(respostaTexto.respostaAssistente());
            if (bytesAudio != null && bytesAudio.length > 0) {
                audioBase64 = Base64.getEncoder().encodeToString(bytesAudio);
            }
        }

        return new RespostaAssistenteResponse(
                textoTranscrito,
                respostaTexto.respostaAssistente(),
                respostaTexto.acaoExecutada(),
                audioBase64,
                LocalDateTime.now()
        );
    }

    private boolean isDemoKey() {
        return apiKey == null || apiKey.trim().isEmpty() || apiKey.equalsIgnoreCase("demo-key");
    }

    /**
     * Fallback de interpretação em linguagem natural sem depender de créditos da OpenAI:
     * Garante que qualquer pessoa testando o projeto localmente ou no GitHub consiga ver
     * o Function Calling funcionando mesmo sem chave de API paga.
     */
    private String processarFallbackLocal(String comando) {
        String lower = comando.toLowerCase();

        // 1. Pergunta sobre saldo / resumo
        if (lower.contains("saldo") || lower.contains("resumo") || lower.contains("quanto sobrou") || lower.contains("orçamento")) {
            ResumoFinanceiroResponse resumo = transacaoService.obterResumoFinanceiro();
            return String.format("RESUMO: Total Receitas: R$ %s | Total Despesas: R$ %s | Saldo Atual: R$ %s | Status: %s | Alerta: %s",
                    resumo.totalReceitas(), resumo.totalDespesas(), resumo.saldoAtual(), resumo.statusOrcamento(), resumo.alertaFinanceiro());
        }

        // 2. Pergunta sobre categoria específica
        if (lower.contains("quanto gastei com") || lower.contains("gastos com")) {
            for (CategoriaTransacao cat : CategoriaTransacao.values()) {
                if (lower.contains(cat.name().toLowerCase())) {
                    return transacaoService.consultarGastosPorCategoria(cat);
                }
            }
        }

        // 3. Registro de transação (detecta valores em reais)
        Pattern pattern = Pattern.compile("(\\d+([.,]\\d{1,2})?)");
        Matcher matcher = pattern.matcher(comando);
        BigDecimal valor = new BigDecimal("50.00");
        if (matcher.find()) {
            String valorStr = matcher.group(1).replace(",", ".");
            try {
                valor = new BigDecimal(valorStr);
            } catch (Exception ignored) {}
        }

        TipoTransacao tipo = (lower.contains("recebi") || lower.contains("ganhei") || lower.contains("salário") || lower.contains("salario"))
                ? TipoTransacao.RECEITA
                : TipoTransacao.DESPESA;

        CategoriaTransacao categoria = CategoriaTransacao.OUTROS;
        if (lower.contains("mercado") || lower.contains("almoço") || lower.contains("jantar") || lower.contains("padaria") || lower.contains("comida")) {
            categoria = CategoriaTransacao.ALIMENTACAO;
        } else if (lower.contains("uber") || lower.contains("gasolina") || lower.contains("ônibus") || lower.contains("combustível")) {
            categoria = CategoriaTransacao.TRANSPORTE;
        } else if (lower.contains("aluguel") || lower.contains("luz") || lower.contains("água") || lower.contains("internet")) {
            categoria = CategoriaTransacao.MORADIA;
        } else if (lower.contains("salário") || lower.contains("salario") || lower.contains("freela")) {
            categoria = CategoriaTransacao.SALARIO;
        } else if (lower.contains("cinema") || lower.contains("festa") || lower.contains("jogo") || lower.contains("passeio")) {
            categoria = CategoriaTransacao.LAZER;
        }

        TransacaoResponse response = transacaoService.registrarTransacao(comando, valor, tipo, categoria);
        return String.format("SUCESSO: Transação #%d cadastrada. Descrição: '%s', Valor: R$ %s, Tipo: %s, Categoria: %s. Continue registrando seus gastos para manter suas finanças sob controle!",
                response.id(), response.descricao(), response.valor(), response.tipo(), response.categoria());
    }
}

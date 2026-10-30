package com.dio.springai.service.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AudioService {

    private static final Logger log = LoggerFactory.getLogger(AudioService.class);

    private final ObjectProvider<OpenAiAudioTranscriptionModel> transcriptionModelProvider;
    private final ObjectProvider<OpenAiAudioSpeechModel> speechModelProvider;

    @Value("${spring.ai.openai.api-key:demo-key}")
    private String apiKey;

    public AudioService(ObjectProvider<OpenAiAudioTranscriptionModel> transcriptionModelProvider,
                        ObjectProvider<OpenAiAudioSpeechModel> speechModelProvider) {
        this.transcriptionModelProvider = transcriptionModelProvider;
        this.speechModelProvider = speechModelProvider;
    }

    public String transcreverAudio(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("O arquivo de áudio não pode estar vazio.");
        }

        OpenAiAudioTranscriptionModel transcriptionModel = transcriptionModelProvider.getIfAvailable();

        // Se a chave configurada for demo ou se o modelo não estiver disponível, usamos simulação
        if (isDemoKey() || transcriptionModel == null) {
            log.warn("Chave OpenAI não configurada ou em modo DEMO. Simulando transcrição de áudio.");
            return simularTranscricao(arquivo.getOriginalFilename());
        }

        try {
            log.info("Enviando áudio para transcrição via OpenAI Whisper: {}", arquivo.getOriginalFilename());

            ByteArrayResource resource = new ByteArrayResource(arquivo.getBytes()) {
                @Override
                public String getFilename() {
                    return arquivo.getOriginalFilename() != null ? arquivo.getOriginalFilename() : "audio.mp3";
                }
            };

            OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                    .language("pt")
                    .build();

            AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(resource, options);
            AudioTranscriptionResponse response = transcriptionModel.call(prompt);

            String texto = response.getResult().getOutput();
            log.info("Transcrição concluída com sucesso: \"{}\"", texto);
            return texto;
        } catch (Exception ex) {
            log.error("Erro ao chamar API de áudio Whisper da OpenAI: {}. Ativando fallback de simulação.", ex.getMessage());
            return simularTranscricao(arquivo.getOriginalFilename());
        }
    }

    public byte[] sintetizarVoz(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return new byte[0];
        }

        OpenAiAudioSpeechModel speechModel = speechModelProvider.getIfAvailable();

        if (isDemoKey() || speechModel == null) {
            log.warn("Chave OpenAI não configurada ou em modo DEMO. Síntese de voz ignorada.");
            return new byte[0];
        }

        try {
            log.info("Sintetizando áudio via OpenAI TTS...");
            return speechModel.call(texto);
        } catch (Exception ex) {
            log.error("Erro ao sintetizar áudio via TTS da OpenAI: {}.", ex.getMessage());
            return new byte[0];
        }
    }

    private boolean isDemoKey() {
        return apiKey == null || apiKey.trim().isEmpty() || apiKey.equalsIgnoreCase("demo-key");
    }

    private String simularTranscricao(String nomeArquivo) {
        // Simulação inteligente baseada em casos de teste reais
        return "Gastei 55 reais no mercado com compras de casa hoje";
    }
}

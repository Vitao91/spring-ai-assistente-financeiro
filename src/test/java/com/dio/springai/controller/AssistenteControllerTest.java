package com.dio.springai.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AssistenteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve processar comando de texto e registrar movimentação financeira")
    void deveProcessarComandoTexto() throws Exception {
        String jsonPayload = """
            {
                "comando": "Gastei 85 reais no restaurante hoje com almoço"
            }
            """;

        mockMvc.perform(post("/api/assistente/comando-texto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.respostaAssistente").exists())
                .andExpect(jsonPath("$.acaoExecutada").exists());
    }

    @Test
    @DisplayName("Deve consultar o resumo financeiro com sucesso")
    void deveConsultarResumoFinanceiro() throws Exception {
        mockMvc.perform(get("/api/assistente/resumo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoAtual").exists())
                .andExpect(jsonPath("$.statusOrcamento").exists());
    }
}

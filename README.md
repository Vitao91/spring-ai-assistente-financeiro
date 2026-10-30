# 🎙️ Assistente Financeiro Inteligente com Spring AI & Voz

API inteligente desenvolvida com **Java 21**, **Spring Boot 3** e **Spring AI**, capaz de interpretar comandos de voz e texto para gerenciar orçamentos pessoais, acionar funções reais de negócio através de **Tool / Function Calling** e sintetizar respostas em áudio.

Projeto final desenvolvido para o desafio de **Spring AI e Engenharia de Prompts** na plataforma da [Digital Innovation One (DIO)](https://dio.me).

---

## 🚀 O que o Projeto Faz?

O **Assistente Financeiro** atua como um copiloto financeiro em linguagem natural. Em vez de preencher formulários manuais para registrar despesas, o usuário simplesmente envia um áudio ou mensagem de texto (ex: *"Gastei 55 reais no mercado com compras de casa hoje"*).

A aplicação realiza o seguinte fluxo ponta a ponta:

```
[Áudio do Usuário (.mp3/.wav)]
              │
              ▼
  1. Transcrição de Áudio (OpenAI Whisper)
              │
              ▼
  2. Raciocínio & Decisão (Spring AI ChatClient / LLM)
              │
              ▼
  3. Function / Tool Calling (Chamada a métodos Java reais)
     - registrarTransacao(descricao, valor, tipo, categoria)
     - consultarResumoFinanceiro()
     - consultarGastosPorCategoria(categoria)
              │
              ▼
  4. Persistência de Dados (Spring Data JPA + Banco H2)
              │
              ▼
  5. Síntese de Voz Opcional (OpenAI TTS) & Resposta Estruturada
```

---

## 🛠️ Tecnologias Utilizadas

- **Java 21** (LTS)
- **Spring Boot 3.4.1**
- **Spring AI (OpenAI Starter 1.0.0-M5)**
  - `ChatClient` com System Prompt especializado
  - `Function Calling` / `@Description` para execução de código Java
  - `OpenAiAudioTranscriptionModel` (Whisper para Speech-to-Text)
  - `OpenAiAudioSpeechModel` (TTS para Text-to-Speech)
- **Spring Data JPA** & **H2 Database** (persistência em memória com console web)
- **Springdoc OpenAPI / Swagger UI 2.8.1** (documentação interativa da API)
- **Bean Validation** (validação de regras de entrada)
- **JUnit 5 & MockMvc** (testes automatizados de fluxo e regras)
- **Maven 3.9+**

---

## 💡 Melhorias Implementadas (Diferenciais do Projeto)

Além do fluxo base apresentado no módulo do expert Thiago Poiani, implementei as seguintes evoluções de negócio e arquitetura:

1. **Classificação Automática por Categorias de Orçamento**:
   - Mapeamento dinâmico para categorias reais (`ALIMENTACAO`, `TRANSPORTE`, `MORADIA`, `SAUDE`, `EDUCACAO`, `LAZER`, `SALARIO`, `INVESTIMENTO`, `OUTROS`).
2. **Motor de Consultoria Orçamentária e Alertas de Saldo**:
   - O método de resumo calcula receitas vs. despesas e classifica o estado atual em `SUPERAVIT`, `EQUILIBRADO` ou `DEFICIT`, gerando avisos automáticos e recomendações de corte de gastos quando o saldo fica negativo.
3. **Modo Híbrido (Real / Simulação Inteligente)**:
   - Se a chave `OPENAI_API_KEY` for informada, o sistema conecta diretamente aos modelos Whisper, GPT e TTS da OpenAI.
   - Caso o projeto seja testado sem chave (`demo-key`), um motor de regras local assume o processamento e o Tool Calling sem estourar exceções, permitindo que qualquer avaliador teste a aplicação 100% sem custos.
4. **Documentação Swagger UI Integrada**:
   - Endpoints totalmente documentados com schemas, exemplos de payloads e envio de arquivos multipart para upload de áudio.

---

## 📋 Como Executar a Aplicação

### Pré-requisitos
- **Java 21** instalado (`java -version`)
- **Maven** instalado (`mvn -version`)

### 1. Clonar o repositório
```bash
git clone https://github.com/Vitao91/spring-ai-assistente-financeiro.git
cd spring-ai-assistente-financeiro
```

### 2. Configurar a Chave OpenAI (Opcional)
Se você possui créditos na OpenAI e quer usar os modelos ao vivo:
```bash
# No Windows (PowerShell):
$env:OPENAI_API_KEY="sk-proj-sua-chave-aqui"

# No Linux/Mac:
export OPENAI_API_KEY="sk-proj-sua-chave-aqui"
```
> *Nota: Caso não configure a variável, a aplicação iniciará normalmente em **Modo Simulação**, pronta para testes locais!*

### 3. Compilar e Rodar
```bash
mvn clean spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

---

## 🧪 Como Testar o Fluxo Principal

### 1. Pelo Swagger UI (Navegador)
Acesse:
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

No Swagger, você pode:
- Executar `POST /api/assistente/comando-texto` enviando frases como:
  - *"Recebi 5000 reais do meu salário"*
  - *"Gastei 65 reais na farmácia com remédios"*
  - *"Quanto é o meu saldo atual e como está meu orçamento?"*
- Executar `POST /api/assistente/comando-voz` enviando um arquivo de áudio (`.mp3` ou `.wav`).
- Consultar `GET /api/assistente/resumo` para ver o consolidado financeiro.
- Acessar o banco em memória no console H2: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:orcamentodb`).

### 2. Exemplo via cURL

**Registrar Despesa via Texto:**
```bash
curl -X POST http://localhost:8080/api/assistente/comando-texto \
  -H "Content-Type: application/json" \
  -d '{"comando": "Gastei 45 reais no almoço de hoje"}'
```

**Consultar Resumo Financeiro:**
```bash
curl -X GET http://localhost:8080/api/assistente/resumo
```

---

## 🧠 O Que Aprendi com Este Desafio

- **Integração Real de IA no Backend:** Como conectar LLMs a um microsserviço Java usando as abstrações de alto nível do Spring AI (`ChatClient`).
- **Function / Tool Calling:** Como permitir que um modelo de IA invoque funções Java reais de forma determinística, integrando inteligência generativa com persistência relacional (Spring Data JPA).
- **Processamento Multimodal de Áudio:** Como converter áudio em texto (Speech-to-Text via Whisper) e sintetizar texto em fala (Text-to-Speech), orquestrando um assistente de voz completo.
- **Resiliência e Fallbacks:** Boas práticas de tratamento de APIs externas com chave dinâmica e fallbacks locais para garantir disponibilidade e testabilidade.

---

## 📸 Demonstração do Sistema

<img width="1886" height="902" alt="Captura de tela 2026-10-30 181040" src="https://github.com/user-attachments/assets/ae28c624-235a-43e5-8a6b-fc603370b93f" />


---

## 👤 Autor

**Victor Antonio Campos**  
- GitHub: [@Vitao91](https://github.com/Vitao91)  
- LinkedIn: [Victor Antonio Campos](https://www.linkedin.com/in/victor-campos-12a806330/)  
- E-mail: `victor78antonio.com@gmail.com`

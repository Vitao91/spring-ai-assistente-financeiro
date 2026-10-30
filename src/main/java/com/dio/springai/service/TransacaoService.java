package com.dio.springai.service;

import com.dio.springai.model.*;
import com.dio.springai.repository.TransacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    private static final Logger log = LoggerFactory.getLogger(TransacaoService.class);

    private final TransacaoRepository repository;

    public TransacaoService(TransacaoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TransacaoResponse registrarTransacao(String descricao, BigDecimal valor, TipoTransacao tipo, CategoriaTransacao categoria) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser positivo.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            descricao = "Movimentação em " + (categoria != null ? categoria.name() : "OUTROS");
        }
        if (tipo == null) {
            tipo = TipoTransacao.DESPESA;
        }
        if (categoria == null) {
            categoria = CategoriaTransacao.OUTROS;
        }

        Transacao transacao = new Transacao(descricao.trim(), valor.setScale(2, RoundingMode.HALF_UP), tipo, categoria);
        Transacao salva = repository.save(transacao);

        log.info("Transação ID {} registrada com sucesso: {} - R$ {} ({})",
                salva.getId(), salva.getDescricao(), salva.getValor(), salva.getTipo());

        return TransacaoResponse.fromEntity(salva);
    }

    @Transactional(readOnly = true)
    public List<TransacaoResponse> listarTodas() {
        return repository.findAllByOrderByDataHoraDesc()
                .stream()
                .map(TransacaoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResumoFinanceiroResponse obterResumoFinanceiro() {
        List<Transacao> todas = repository.findAll();

        BigDecimal totalReceitas = todas.stream()
                .filter(t -> t.getTipo() == TipoTransacao.RECEITA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalDespesas = todas.stream()
                .filter(t -> t.getTipo() == TipoTransacao.DESPESA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal saldoAtual = totalReceitas.subtract(totalDespesas).setScale(2, RoundingMode.HALF_UP);

        Map<CategoriaTransacao, BigDecimal> despesasPorCategoria = new EnumMap<>(CategoriaTransacao.class);
        todas.stream()
                .filter(t -> t.getTipo() == TipoTransacao.DESPESA)
                .forEach(t -> despesasPorCategoria.merge(t.getCategoria(), t.getValor(), BigDecimal::add));

        String status;
        String alerta;

        if (saldoAtual.compareTo(BigDecimal.ZERO) > 0) {
            status = "SUPERAVIT";
            alerta = "Parabéns! Suas receitas superam as despesas. Considere poupar ou investir a sobra.";
        } else if (saldoAtual.compareTo(BigDecimal.ZERO) == 0) {
            status = "EQUILIBRADO";
            alerta = "Atenção: Seu saldo está exatamente zerado. Evite novos gastos não essenciais.";
        } else {
            status = "DEFICIT";
            alerta = "ALERTA: Seu saldo está negativo em R$ " + saldoAtual.abs() + "! Priorize cortar gastos supérfluos.";
        }

        return new ResumoFinanceiroResponse(
                totalReceitas,
                totalDespesas,
                saldoAtual,
                todas.size(),
                despesasPorCategoria,
                status,
                alerta
        );
    }

    @Transactional(readOnly = true)
    public String consultarGastosPorCategoria(CategoriaTransacao categoria) {
        List<Transacao> lista = repository.findByCategoria(categoria);
        BigDecimal total = lista.stream()
                .filter(t -> t.getTipo() == TipoTransacao.DESPESA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return String.format("Total gasto na categoria %s: R$ %s em %d movimentações.",
                categoria.name(), total, lista.size());
    }
}

package com.dio.springai.repository;

import com.dio.springai.model.CategoriaTransacao;
import com.dio.springai.model.TipoTransacao;
import com.dio.springai.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByTipo(TipoTransacao tipo);

    List<Transacao> findByCategoria(CategoriaTransacao categoria);

    List<Transacao> findAllByOrderByDataHoraDesc();
}

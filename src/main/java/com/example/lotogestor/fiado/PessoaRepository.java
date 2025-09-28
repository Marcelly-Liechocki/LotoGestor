package com.example.lotogestor.fiado;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
  boolean existsByNomeIgnoreCase(String nome);
}


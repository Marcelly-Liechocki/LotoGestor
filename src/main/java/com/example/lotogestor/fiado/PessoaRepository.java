package com.example.lotogestor.fiado;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
  boolean existsByNomeIgnoreCase(String nome);
  Optional<Pessoa> findByNomeIgnoreCase(String nome);
}

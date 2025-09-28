package com.example.lotogestor.caixa;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface AberturaCaixaRepository extends JpaRepository<AberturaCaixa, Long> {
  boolean existsByData(LocalDate data);
}

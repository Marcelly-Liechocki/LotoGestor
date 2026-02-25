package com.example.lotogestor.fiado;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FiadoRepository extends JpaRepository<Fiado, Long> {
  List<Fiado> findByOperadorId(Long operadorId);
  List<Fiado> findByDataBetween(LocalDate inicio, LocalDate fim);
}

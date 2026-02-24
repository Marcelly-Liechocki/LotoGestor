package com.example.lotogestor.api;

import com.example.lotogestor.fiado.Fiado;
import com.example.lotogestor.fiado.Pessoa;
import java.util.List;

public record DashboardResponse(
  List<Fiado> fiados,
  List<Pessoa> pessoas,
  boolean hasCaixaAberto
) {}

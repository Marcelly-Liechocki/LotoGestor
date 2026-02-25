package com.example.lotogestor.api.admin;

import com.example.lotogestor.fiado.Fiado;
import com.example.lotogestor.fiado.FiadoStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FiadoAdminResponse(
  Long id,
  LocalDate data,
  String pessoa,
  String operador,
  BigDecimal valor,
  String descricao,
  FiadoStatus status,
  LocalDateTime dataPagamento
) {
  public static FiadoAdminResponse from(Fiado fiado) {
    String operadorNome = fiado.getOperador() == null ? "" : fiado.getOperador().getNomeCompleto();
    return new FiadoAdminResponse(
      fiado.getId(),
      fiado.getData(),
      fiado.getPessoa(),
      operadorNome,
      fiado.getValor(),
      fiado.getDescricao(),
      fiado.getStatus(),
      fiado.getDataPagamento()
    );
  }
}

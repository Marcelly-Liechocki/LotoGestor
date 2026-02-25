package com.example.lotogestor.fiado;

import com.example.lotogestor.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Fiado {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull private LocalDate data = LocalDate.now();
  @NotBlank private String pessoa;
  @NotNull @DecimalMin("0.01") private BigDecimal valor;
  @Size(max=255) private String descricao;
  @Enumerated(EnumType.STRING)
  private FiadoStatus status = FiadoStatus.PENDENTE;
  private LocalDateTime dataPagamento;

  @ManyToOne
  @JoinColumn(name = "operador_id")
  private Usuario operador;

  public Long getId(){return id;}
  public LocalDate getData(){return data;}
  public void setData(LocalDate d){this.data=d;}
  public String getPessoa(){return pessoa;}
  public void setPessoa(String p){this.pessoa=p;}
  public BigDecimal getValor(){return valor;}
  public void setValor(BigDecimal v){this.valor=v;}
  public String getDescricao(){return descricao;}
  public void setDescricao(String d){this.descricao=d;}
  public FiadoStatus getStatus(){return status;}
  public void setStatus(FiadoStatus status){this.status=status;}
  public LocalDateTime getDataPagamento(){return dataPagamento;}
  public void setDataPagamento(LocalDateTime dataPagamento){this.dataPagamento=dataPagamento;}
  public Usuario getOperador(){return operador;}
  public void setOperador(Usuario operador){this.operador=operador;}
}

package com.example.lotogestor.caixa;

import com.example.lotogestor.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class AberturaCaixa {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull private LocalDate data = LocalDate.now();

  @NotNull private BigDecimal moedas;
  @NotNull private BigDecimal dinheiro;
  @NotNull private BigDecimal bolao;
  @NotNull private BigDecimal instantaneas;
  @NotNull private BigDecimal telesena;
  @NotNull private BigDecimal fiados;
  @NotNull private BigDecimal deposito;
  @NotNull private BigDecimal retirada;

  @ManyToOne
  @JoinColumn(name = "operador_id")
  private Usuario operador;

  public Long getId(){return id;}
  public LocalDate getData(){return data;}
  public void setData(LocalDate d){this.data=d;}
  public BigDecimal getMoedas(){return moedas;}
  public void setMoedas(BigDecimal v){this.moedas=v;}
  public BigDecimal getDinheiro(){return dinheiro;}
  public void setDinheiro(BigDecimal v){this.dinheiro=v;}
  public BigDecimal getBolao(){return bolao;}
  public void setBolao(BigDecimal v){this.bolao=v;}
  public BigDecimal getInstantaneas(){return instantaneas;}
  public void setInstantaneas(BigDecimal v){this.instantaneas=v;}
  public BigDecimal getTelesena(){return telesena;}
  public void setTelesena(BigDecimal v){this.telesena=v;}
  public BigDecimal getFiados(){return fiados;}
  public void setFiados(BigDecimal v){this.fiados=v;}
  public BigDecimal getDeposito(){return deposito;}
  public void setDeposito(BigDecimal v){this.deposito=v;}
  public BigDecimal getRetirada(){return retirada;}
  public void setRetirada(BigDecimal v){this.retirada=v;}
  public Usuario getOperador(){return operador;}
  public void setOperador(Usuario operador){this.operador=operador;}
}

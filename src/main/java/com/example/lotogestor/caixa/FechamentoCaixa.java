package com.example.lotogestor.caixa;

import com.example.lotogestor.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class FechamentoCaixa {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull private LocalDate data = LocalDate.now();

@NotNull private BigDecimal dinheiro;
@NotNull private BigDecimal cheques;
@NotNull private BigDecimal depositos;
@NotNull private BigDecimal fiados;
@NotNull private BigDecimal totalMoedas;
@NotNull private BigDecimal bolao;
@NotNull private BigDecimal telesena;
@NotNull private BigDecimal instantaneas;
@NotNull private BigDecimal telesenasTrocadas;

  @ManyToOne
  @JoinColumn(name = "operador_id")
  private Usuario operador;

  public Long getId(){return id;}
  public LocalDate getData(){return data;}
  public void setData(LocalDate d){this.data=d;}
  public BigDecimal getDinheiro(){return dinheiro;}
  public void setDinheiro(BigDecimal v){this.dinheiro=v;}
  public BigDecimal getCheques(){return cheques;}
  public void setCheques(BigDecimal v){this.cheques=v;}
  public BigDecimal getDepositos(){return depositos;}
  public void setDepositos(BigDecimal v){this.depositos=v;}
  public BigDecimal getFiados(){return fiados;}
  public void setFiados(BigDecimal v){this.fiados=v;}
  public BigDecimal getTotalMoedas(){return totalMoedas;}
  public void setTotalMoedas(BigDecimal v){this.totalMoedas=v;}
  public BigDecimal getBolao(){return bolao;}
  public void setBolao(BigDecimal v){this.bolao=v;}
  public BigDecimal getTelesena(){return telesena;}
  public void setTelesena(BigDecimal v){this.telesena=v;}
  public BigDecimal getInstantaneas(){return instantaneas;}
  public void setInstantaneas(BigDecimal v){this.instantaneas=v;}
  public BigDecimal getTelesenasTrocadas(){return telesenasTrocadas;}
  public void setTelesenasTrocadas(BigDecimal v){this.telesenasTrocadas=v;}
  public Usuario getOperador(){return operador;}
  public void setOperador(Usuario operador){this.operador=operador;}
}

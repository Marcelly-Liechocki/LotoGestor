package com.example.lotogestor.fiado;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
public class Pessoa {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(unique = true)
  private String nome;

  private LocalDateTime criadoEm = LocalDateTime.now();

  // getters/setters
  public Long getId(){ return id; }
  public String getNome(){ return nome; }
  public void setNome(String nome){ this.nome = nome; }
  public LocalDateTime getCriadoEm(){ return criadoEm; }
  public void setCriadoEm(LocalDateTime t){ this.criadoEm = t; }
}

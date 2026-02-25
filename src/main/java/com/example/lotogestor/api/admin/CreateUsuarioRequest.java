package com.example.lotogestor.api.admin;

import com.example.lotogestor.usuario.UsuarioRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUsuarioRequest(
  @NotBlank String nomeCompleto,
  @Email @NotBlank String email,
  @NotBlank String endereco,
  @NotBlank String cpf,
  @NotBlank String senha,
  @NotNull UsuarioRole role,
  String fotoUrl
) {}

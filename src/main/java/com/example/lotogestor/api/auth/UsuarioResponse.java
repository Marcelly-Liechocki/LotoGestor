package com.example.lotogestor.api.auth;

import com.example.lotogestor.usuario.Usuario;
import com.example.lotogestor.usuario.UsuarioRole;

public record UsuarioResponse(
  Long id,
  String nomeCompleto,
  String email,
  String endereco,
  String cpf,
  UsuarioRole role,
  String fotoUrl
) {
  public static UsuarioResponse from(Usuario usuario) {
    return new UsuarioResponse(
      usuario.getId(),
      usuario.getNomeCompleto(),
      usuario.getEmail(),
      usuario.getEndereco(),
      usuario.getCpf(),
      usuario.getRole(),
      usuario.getFotoUrl()
    );
  }
}

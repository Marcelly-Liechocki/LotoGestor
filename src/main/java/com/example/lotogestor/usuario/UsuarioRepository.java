package com.example.lotogestor.usuario;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  Optional<Usuario> findByEmail(String email);
  boolean existsByEmail(String email);
  boolean existsByCpf(String cpf);
  List<Usuario> findByRole(UsuarioRole role);
}

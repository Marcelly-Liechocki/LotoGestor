package com.example.lotogestor.usuario;

import java.util.Optional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UserDetailsService {

  private final UsuarioRepository usuarios;
  private final PasswordEncoder encoder;

  public UsuarioService(UsuarioRepository usuarios, PasswordEncoder encoder) {
    this.usuarios = usuarios;
    this.encoder = encoder;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Usuario usuario = usuarios.findByEmail(username)
      .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));

    return User.withUsername(usuario.getEmail())
      .password(usuario.getSenhaHash())
      .roles(usuario.getRole().name())
      .build();
  }

  public Usuario criarUsuario(Usuario usuario, String senha) {
    usuario.setSenhaHash(encoder.encode(senha));
    return usuarios.save(usuario);
  }

  public Optional<Usuario> buscarPorEmail(String email) {
    return usuarios.findByEmail(email);
  }
}

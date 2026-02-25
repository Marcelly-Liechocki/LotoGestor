package com.example.lotogestor.api.auth;

import com.example.lotogestor.usuario.Usuario;
import com.example.lotogestor.usuario.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authManager;
  private final UsuarioService usuarios;

  public AuthController(AuthenticationManager authManager, UsuarioService usuarios) {
    this.authManager = authManager;
    this.usuarios = usuarios;
  }

  @PostMapping("/login")
  public UsuarioResponse login(@Valid @RequestBody LoginRequest request,
                               HttpServletRequest httpRequest) {
    Authentication auth = authManager.authenticate(
      new UsernamePasswordAuthenticationToken(request.email(), request.senha())
    );
    SecurityContextHolder.getContext().setAuthentication(auth);
    httpRequest.getSession(true)
      .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        SecurityContextHolder.getContext());

    Usuario usuario = usuarios.buscarPorEmail(request.email())
      .orElseThrow();
    return UsuarioResponse.from(usuario);
  }

  @PostMapping("/logout")
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
    new SecurityContextLogoutHandler().logout(request, response, auth);
  }

  @GetMapping("/me")
  public UsuarioResponse me(Authentication auth) {
    if (auth == null) {
      return null;
    }
    Usuario usuario = usuarios.buscarPorEmail(auth.getName())
      .orElseThrow();
    return UsuarioResponse.from(usuario);
  }
}

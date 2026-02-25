package com.example.lotogestor.api.admin;

import com.example.lotogestor.api.auth.UsuarioResponse;
import com.example.lotogestor.caixa.FechamentoCaixa;
import com.example.lotogestor.caixa.FechamentoCaixaRepository;
import com.example.lotogestor.fiado.Fiado;
import com.example.lotogestor.fiado.FiadoRepository;
import com.example.lotogestor.fiado.FiadoStatus;
import com.example.lotogestor.usuario.Usuario;
import com.example.lotogestor.usuario.UsuarioRepository;
import com.example.lotogestor.usuario.UsuarioRole;
import com.example.lotogestor.usuario.UsuarioService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private final UsuarioRepository usuarios;
  private final UsuarioService usuarioService;
  private final FechamentoCaixaRepository fechamentos;
  private final FiadoRepository fiados;
  private final PasswordEncoder encoder;

  public AdminController(UsuarioRepository usuarios,
                         UsuarioService usuarioService,
                         FechamentoCaixaRepository fechamentos,
                         FiadoRepository fiados,
                         PasswordEncoder encoder) {
    this.usuarios = usuarios;
    this.usuarioService = usuarioService;
    this.fechamentos = fechamentos;
    this.fiados = fiados;
    this.encoder = encoder;
  }

  @PostMapping("/usuarios")
  @ResponseStatus(HttpStatus.CREATED)
  public UsuarioResponse criarUsuario(@Valid @RequestBody CreateUsuarioRequest request) {
    if (request.role() == UsuarioRole.ADMIN) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin nao pode ser criado aqui");
    }
    if (usuarios.existsByEmail(request.email())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email ja cadastrado");
    }
    if (usuarios.existsByCpf(request.cpf())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF ja cadastrado");
    }

    Usuario usuario = new Usuario();
    usuario.setNomeCompleto(request.nomeCompleto());
    usuario.setEmail(request.email());
    usuario.setEndereco(request.endereco());
    usuario.setCpf(request.cpf());
    usuario.setRole(request.role());
    usuario.setFotoUrl(request.fotoUrl());

    Usuario salvo = usuarioService.criarUsuario(usuario, request.senha());
    return UsuarioResponse.from(salvo);
  }

  @PutMapping("/usuarios/{id}")
  public UsuarioResponse atualizarUsuario(@PathVariable Long id,
                                          @Valid @RequestBody UpdateUsuarioRequest request) {
    Usuario usuario = usuarios.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
    if (request.role() == UsuarioRole.ADMIN) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin nao pode ser definido aqui");
    }
    if (!usuario.getEmail().equalsIgnoreCase(request.email()) && usuarios.existsByEmail(request.email())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email ja cadastrado");
    }
    if (!usuario.getCpf().equalsIgnoreCase(request.cpf()) && usuarios.existsByCpf(request.cpf())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF ja cadastrado");
    }

    usuario.setNomeCompleto(request.nomeCompleto());
    usuario.setEmail(request.email());
    usuario.setEndereco(request.endereco());
    usuario.setCpf(request.cpf());
    usuario.setRole(request.role());
    usuario.setFotoUrl(request.fotoUrl());
    if (request.senha() != null && !request.senha().isBlank()) {
      usuario.setSenhaHash(encoder.encode(request.senha()));
    }

    Usuario salvo = usuarios.save(usuario);
    return UsuarioResponse.from(salvo);
  }

  @DeleteMapping("/usuarios/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluirUsuario(@PathVariable Long id) {
    Usuario usuario = usuarios.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
    if (usuario.getRole() == UsuarioRole.ADMIN) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin nao pode ser removido");
    }
    usuarios.delete(usuario);
  }

  @GetMapping("/operadores")
  public List<UsuarioResponse> listarOperadores() {
    return usuarios.findByRole(UsuarioRole.OPERADOR).stream()
      .sorted(Comparator.comparing(Usuario::getNomeCompleto))
      .map(UsuarioResponse::from)
      .collect(Collectors.toList());
  }

  @GetMapping("/dashboard")
  public AdminDashboardResponse dashboard() {
    BigDecimal instantaneas = BigDecimal.ZERO;
    BigDecimal marketplace = BigDecimal.ZERO;
    BigDecimal telesenas = BigDecimal.ZERO;

    for (FechamentoCaixa fechamento : fechamentos.findAll()) {
      if (fechamento.getInstantaneas() != null) {
        instantaneas = instantaneas.add(fechamento.getInstantaneas());
      }
      if (fechamento.getBolao() != null) {
        marketplace = marketplace.add(fechamento.getBolao());
      }
      if (fechamento.getTelesena() != null) {
        telesenas = telesenas.add(fechamento.getTelesena());
      }
    }

    BigDecimal total = instantaneas.add(marketplace).add(telesenas);
    return new AdminDashboardResponse(instantaneas, marketplace, telesenas, total);
  }

  @GetMapping("/fiados")
  public List<FiadoAdminResponse> listarFiados(
    @RequestParam Optional<Long> operadorId,
    @RequestParam Optional<String> pessoa,
    @RequestParam Optional<String> dataInicio,
    @RequestParam Optional<String> dataFim,
    @RequestParam Optional<FiadoStatus> status
  ) {
    List<Fiado> resultado = fiados.findAll();

    if (operadorId.isPresent()) {
      Long id = operadorId.get();
      resultado = resultado.stream()
        .filter(fiado -> fiado.getOperador() != null && id.equals(fiado.getOperador().getId()))
        .collect(Collectors.toList());
    }

    if (pessoa.isPresent() && !pessoa.get().isBlank()) {
      String filtro = pessoa.get().trim().toLowerCase();
      resultado = resultado.stream()
        .filter(fiado -> fiado.getPessoa() != null && fiado.getPessoa().toLowerCase().contains(filtro))
        .collect(Collectors.toList());
    }

    if (dataInicio.isPresent() && dataFim.isPresent()) {
      LocalDate inicio = LocalDate.parse(dataInicio.get());
      LocalDate fim = LocalDate.parse(dataFim.get());
      resultado = resultado.stream()
        .filter(fiado -> fiado.getData() != null && !fiado.getData().isBefore(inicio) && !fiado.getData().isAfter(fim))
        .collect(Collectors.toList());
    }

    if (status.isPresent()) {
      resultado = resultado.stream()
        .filter(fiado -> status.get().equals(fiado.getStatus()))
        .collect(Collectors.toList());
    }

    return resultado.stream()
      .sorted(Comparator.comparing(Fiado::getData).reversed())
      .map(FiadoAdminResponse::from)
      .collect(Collectors.toList());
  }
}

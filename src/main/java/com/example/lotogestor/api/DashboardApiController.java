package com.example.lotogestor.api;

import com.example.lotogestor.caixa.AberturaCaixa;
import com.example.lotogestor.caixa.AberturaCaixaRepository;
import com.example.lotogestor.caixa.FechamentoCaixa;
import com.example.lotogestor.caixa.FechamentoCaixaRepository;
import com.example.lotogestor.fiado.Fiado;
import com.example.lotogestor.fiado.FiadoRepository;
import com.example.lotogestor.fiado.Pessoa;
import com.example.lotogestor.fiado.PessoaRepository;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DashboardApiController {

  private final AberturaCaixaRepository aberturas;
  private final FiadoRepository fiados;
  private final FechamentoCaixaRepository fechamentos;
  private final PessoaRepository pessoas;

  public DashboardApiController(AberturaCaixaRepository aberturas,
                                FiadoRepository fiados,
                                FechamentoCaixaRepository fechamentos,
                                PessoaRepository pessoas) {
    this.aberturas = aberturas;
    this.fiados = fiados;
    this.fechamentos = fechamentos;
    this.pessoas = pessoas;
  }

  @GetMapping("/dashboard")
  public DashboardResponse dashboard() {
    List<Fiado> listaFiados = fiados.findAll();
    List<Pessoa> listaPessoas = pessoas.findAll();
    boolean hasCaixaAberto = aberturas.existsByData(LocalDate.now());
    return new DashboardResponse(listaFiados, listaPessoas, hasCaixaAberto);
  }

  @GetMapping("/fiados")
  public List<Fiado> listarFiados() {
    return fiados.findAll();
  }

  @PostMapping("/fiados")
  public ResponseEntity<Fiado> criarFiado(@Valid @RequestBody Fiado fiado) {
    Fiado salvo = fiados.save(fiado);
    return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
  }

  @DeleteMapping("/fiados/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void apagarFiado(@PathVariable Long id) {
    fiados.deleteById(id);
  }

  @GetMapping("/pessoas")
  public List<Pessoa> listarPessoas() {
    return pessoas.findAll();
  }

  @PostMapping("/pessoas")
  public ResponseEntity<Pessoa> criarPessoa(@Valid @RequestBody PessoaCreateRequest request) {
    String nome = request.nome().trim();
    return pessoas.findByNomeIgnoreCase(nome)
      .map(ResponseEntity::ok)
      .orElseGet(() -> {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(nome);
        Pessoa salva = pessoas.save(pessoa);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
      });
  }

  @PostMapping("/aberturas")
  public ResponseEntity<AberturaCaixa> criarAbertura(@Valid @RequestBody AberturaCaixa abertura) {
    AberturaCaixa salva = aberturas.save(abertura);
    return ResponseEntity.status(HttpStatus.CREATED).body(salva);
  }

  @PostMapping("/fechamentos")
  public ResponseEntity<FechamentoCaixa> criarFechamento(@Valid @RequestBody FechamentoCaixa fechamento) {
    FechamentoCaixa salvo = fechamentos.save(fechamento);
    return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
  }
}

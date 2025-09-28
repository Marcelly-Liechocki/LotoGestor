package com.example.lotogestor.web;

import com.example.lotogestor.caixa.AberturaCaixa;
import com.example.lotogestor.caixa.AberturaCaixaRepository;
import com.example.lotogestor.caixa.FechamentoCaixa;
import com.example.lotogestor.caixa.FechamentoCaixaRepository;
import com.example.lotogestor.fiado.Fiado;
import com.example.lotogestor.fiado.FiadoRepository;
import com.example.lotogestor.fiado.Pessoa;
import com.example.lotogestor.fiado.PessoaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DashboardController {

  private final AberturaCaixaRepository aberturas;
  private final FiadoRepository fiados;
  private final FechamentoCaixaRepository fechamentos;
  private final PessoaRepository pessoas;

  public DashboardController(AberturaCaixaRepository aberturas,
                             FiadoRepository fiados,
                             FechamentoCaixaRepository fechamentos,
                             PessoaRepository pessoas) {
    this.aberturas = aberturas;
    this.fiados = fiados;
    this.fechamentos = fechamentos;
    this.pessoas = pessoas;
  }

  @GetMapping("/login")
  public String login() { return "login"; }

  // ======== Abertura ========
  @PostMapping("/aberturas")
  public String criarAbertura(@Valid AberturaCaixa abertura, BindingResult br, RedirectAttributes ra) {
    if (br.hasErrors()) return "dashboard";
    aberturas.save(abertura);
    ra.addFlashAttribute("msg", "Caixa aberto com sucesso");
    return "redirect:/dashboard#abertura";
  }

  // ======== Pessoas (cadastro rápido) ========
  @PostMapping("/pessoas")
  public String criarPessoa(@RequestParam("nomePessoa") String nome) {
    if (nome != null) {
      String n = nome.trim();
      if (!n.isEmpty() && !pessoas.existsByNomeIgnoreCase(n)) {
        Pessoa p = new Pessoa();
        p.setNome(n);
        pessoas.save(p);
      }
    }
    return "redirect:/dashboard#fiados";
  }

  // ======== Fiados ========
  @PostMapping("/fiados")
  public String criarFiado(@Valid Fiado fiado, BindingResult br, RedirectAttributes ra) {
    if (br.hasErrors()) return "dashboard";
    fiados.save(fiado);
    ra.addFlashAttribute("msg", "Fiado adicionado com sucesso");
    return "redirect:/dashboard#fiados";
  }

  @PostMapping("/fiados/{id}/delete")
  public String apagarFiado(@PathVariable Long id) {
    fiados.deleteById(id);
    return "redirect:/dashboard#fiados";
  }

  // ======== Fechamento ========
  @PostMapping("/fechamentos")
  public String criarFechamento(@Valid FechamentoCaixa fechamento, BindingResult br, RedirectAttributes ra) {
    if (br.hasErrors()) return "dashboard";
    fechamentos.save(fechamento);
    ra.addFlashAttribute("msg", "Caixa fechado com sucesso");
    return "redirect:/dashboard#fechamento";
  }

  // ======== Dashboard (ÚNICO) ========
  @GetMapping({"/", "/dashboard"})
  public String dashboard(Model model) {
    model.addAttribute("abertura", new AberturaCaixa());
    model.addAttribute("novoFiado", new Fiado());
    model.addAttribute("fechamento", new FechamentoCaixa());
    model.addAttribute("listaFiados", fiados.findAll());

    List<Pessoa> listaPessoas = pessoas.findAll();
    model.addAttribute("listaPessoas", listaPessoas);

    boolean hasCaixaAberto = aberturas.existsByData(LocalDate.now());
    model.addAttribute("hasCaixaAberto", hasCaixaAberto);

    return "dashboard";
  }
}

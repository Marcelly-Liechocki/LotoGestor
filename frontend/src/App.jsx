import React, { useEffect, useMemo, useState } from "react";
import {
  createAbertura,
  createFechamento,
  createFiado,
  createPessoa,
  deleteFiado,
  getDashboard
} from "./api.js";

const today = () => new Date().toISOString().slice(0, 10);

const emptyAbertura = {
  data: today(),
  moedas: "0.00",
  dinheiro: "0.00",
  bolao: "0.00",
  instantaneas: "0.00",
  telesena: "0.00",
  fiados: "0.00",
  deposito: "0.00",
  retirada: "0.00"
};

const emptyFechamento = {
  data: today(),
  dinheiro: "0.00",
  cheques: "0.00",
  depositos: "0.00",
  fiados: "0.00",
  totalMoedas: "0.00",
  bolao: "0.00",
  telesena: "0.00",
  instantaneas: "0.00",
  telesenasTrocadas: "0.00"
};

const emptyFiado = {
  data: today(),
  pessoa: "",
  valor: "",
  descricao: ""
};

export default function App() {
  const [dashboard, setDashboard] = useState({
    fiados: [],
    pessoas: [],
    hasCaixaAberto: false
  });
  const [loading, setLoading] = useState(true);
  const [notice, setNotice] = useState("");
  const [error, setError] = useState("");

  const [novaPessoa, setNovaPessoa] = useState("");
  const [novoFiado, setNovoFiado] = useState(emptyFiado);
  const [abertura, setAbertura] = useState(emptyAbertura);
  const [fechamento, setFechamento] = useState(emptyFechamento);

  const pessoasNomes = useMemo(
    () => dashboard.pessoas.map((pessoa) => pessoa.nome),
    [dashboard.pessoas]
  );

  async function loadDashboard() {
    setLoading(true);
    try {
      const data = await getDashboard();
      setDashboard(data);
    } catch (err) {
      setError(err.message || "Erro ao carregar dashboard");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadDashboard();
  }, []);

  function resetMessages() {
    setNotice("");
    setError("");
  }

  async function handlePessoaSubmit(event) {
    event.preventDefault();
    resetMessages();
    try {
      await createPessoa({ nome: novaPessoa });
      setNovaPessoa("");
      setNotice("Pessoa cadastrada com sucesso.");
      await loadDashboard();
    } catch (err) {
      setError(err.message || "Erro ao cadastrar pessoa");
    }
  }

  async function handleFiadoSubmit(event) {
    event.preventDefault();
    resetMessages();
    try {
      await createFiado({
        ...novoFiado,
        valor: Number(novoFiado.valor)
      });
      setNovoFiado(emptyFiado);
      setNotice("Fiado lançado com sucesso.");
      await loadDashboard();
    } catch (err) {
      setError(err.message || "Erro ao lançar fiado");
    }
  }

  async function handleFiadoDelete(id) {
    resetMessages();
    try {
      await deleteFiado(id);
      setNotice("Fiado removido.");
      await loadDashboard();
    } catch (err) {
      setError(err.message || "Erro ao remover fiado");
    }
  }

  async function handleAberturaSubmit(event) {
    event.preventDefault();
    resetMessages();
    try {
      await createAbertura(formatCaixaPayload(abertura));
      setAbertura(emptyAbertura);
      setNotice("Abertura de caixa registrada.");
      await loadDashboard();
    } catch (err) {
      setError(err.message || "Erro ao abrir caixa");
    }
  }

  async function handleFechamentoSubmit(event) {
    event.preventDefault();
    resetMessages();
    try {
      await createFechamento(formatCaixaPayload(fechamento));
      setFechamento(emptyFechamento);
      setNotice("Fechamento de caixa registrado.");
      await loadDashboard();
    } catch (err) {
      setError(err.message || "Erro ao fechar caixa");
    }
  }

  function formatCaixaPayload(values) {
    const payload = { ...values };
    Object.keys(payload).forEach((key) => {
      if (key !== "data") {
        payload[key] = Number(payload[key]);
      }
    });
    return payload;
  }

  return (
    <div className="page">
      <header className="hero">
        <div>
          <p className="eyebrow">LotoGestor</p>
          <h1>Controle de caixa e fiados em um painel simples.</h1>
          <p className="subtitle">
            React no frontend, Spring Boot no backend, com foco em fluxo diario.
          </p>
        </div>
        <div className="status-card">
          <p className="label">Status do caixa</p>
          <p className={`status ${dashboard.hasCaixaAberto ? "open" : "closed"}`}>
            {dashboard.hasCaixaAberto ? "Aberto hoje" : "Sem abertura hoje"}
          </p>
          <p className="hint">Atualizado em tempo real com a API.</p>
        </div>
      </header>

      {(notice || error) && (
        <div className={`banner ${error ? "error" : "success"}`}>
          {error || notice}
        </div>
      )}

      {loading ? (
        <div className="loading">Carregando dados...</div>
      ) : (
        <main className="grid">
          <section className="panel">
            <h2>Cadastro rapido de pessoa</h2>
            <p className="panel-sub">
              Adicione nomes para usar nos lancamentos de fiado.
            </p>
            <form onSubmit={handlePessoaSubmit} className="form">
              <input
                type="text"
                placeholder="Nome completo"
                value={novaPessoa}
                onChange={(event) => setNovaPessoa(event.target.value)}
                required
              />
              <button type="submit">Salvar</button>
            </form>
            <div className="tags">
              {dashboard.pessoas.map((pessoa) => (
                <span key={pessoa.id}>{pessoa.nome}</span>
              ))}
            </div>
          </section>

          <section className="panel">
            <h2>Lancar fiado</h2>
            <p className="panel-sub">Registre vendas fiadas do dia.</p>
            <form onSubmit={handleFiadoSubmit} className="form">
              <input
                type="date"
                value={novoFiado.data}
                onChange={(event) =>
                  setNovoFiado((prev) => ({ ...prev, data: event.target.value }))
                }
                required
              />
              <input
                list="pessoas"
                placeholder="Pessoa"
                value={novoFiado.pessoa}
                onChange={(event) =>
                  setNovoFiado((prev) => ({ ...prev, pessoa: event.target.value }))
                }
                required
              />
              <input
                type="number"
                step="0.01"
                placeholder="Valor"
                value={novoFiado.valor}
                onChange={(event) =>
                  setNovoFiado((prev) => ({ ...prev, valor: event.target.value }))
                }
                required
              />
              <input
                type="text"
                placeholder="Descricao"
                value={novoFiado.descricao}
                onChange={(event) =>
                  setNovoFiado((prev) => ({ ...prev, descricao: event.target.value }))
                }
              />
              <button type="submit">Registrar fiado</button>
            </form>

            <datalist id="pessoas">
              {pessoasNomes.map((nome) => (
                <option key={nome} value={nome} />
              ))}
            </datalist>
          </section>

          <section className="panel span-2">
            <div className="panel-header">
              <h2>Fiados recentes</h2>
              <span className="counter">{dashboard.fiados.length} itens</span>
            </div>
            <div className="table">
              <div className="table-row head">
                <span>Data</span>
                <span>Pessoa</span>
                <span>Valor</span>
                <span>Descricao</span>
                <span></span>
              </div>
              {dashboard.fiados.map((fiado) => (
                <div key={fiado.id} className="table-row">
                  <span>{fiado.data}</span>
                  <span>{fiado.pessoa}</span>
                  <span>R$ {Number(fiado.valor).toFixed(2)}</span>
                  <span>{fiado.descricao || "-"}</span>
                  <button type="button" onClick={() => handleFiadoDelete(fiado.id)}>
                    Remover
                  </button>
                </div>
              ))}
            </div>
          </section>

          <section className="panel">
            <h2>Abertura de caixa</h2>
            <form onSubmit={handleAberturaSubmit} className="form grid-form">
              <input
                type="date"
                value={abertura.data}
                onChange={(event) =>
                  setAbertura((prev) => ({ ...prev, data: event.target.value }))
                }
                required
              />
              {renderMoneyInput(abertura, setAbertura, "moedas", "Moedas")}
              {renderMoneyInput(abertura, setAbertura, "dinheiro", "Dinheiro")}
              {renderMoneyInput(abertura, setAbertura, "bolao", "Bolao")}
              {renderMoneyInput(abertura, setAbertura, "instantaneas", "Instantaneas")}
              {renderMoneyInput(abertura, setAbertura, "telesena", "Telesena")}
              {renderMoneyInput(abertura, setAbertura, "fiados", "Fiados")}
              {renderMoneyInput(abertura, setAbertura, "deposito", "Deposito")}
              {renderMoneyInput(abertura, setAbertura, "retirada", "Retirada")}
              <button type="submit" className="full">Salvar abertura</button>
            </form>
          </section>

          <section className="panel">
            <h2>Fechamento de caixa</h2>
            <form onSubmit={handleFechamentoSubmit} className="form grid-form">
              <input
                type="date"
                value={fechamento.data}
                onChange={(event) =>
                  setFechamento((prev) => ({ ...prev, data: event.target.value }))
                }
                required
              />
              {renderMoneyInput(fechamento, setFechamento, "dinheiro", "Dinheiro")}
              {renderMoneyInput(fechamento, setFechamento, "cheques", "Cheques")}
              {renderMoneyInput(fechamento, setFechamento, "depositos", "Depositos")}
              {renderMoneyInput(fechamento, setFechamento, "fiados", "Fiados")}
              {renderMoneyInput(fechamento, setFechamento, "totalMoedas", "Total moedas")}
              {renderMoneyInput(fechamento, setFechamento, "bolao", "Bolao")}
              {renderMoneyInput(fechamento, setFechamento, "telesena", "Telesena")}
              {renderMoneyInput(fechamento, setFechamento, "instantaneas", "Instantaneas")}
              {renderMoneyInput(
                fechamento,
                setFechamento,
                "telesenasTrocadas",
                "Telesenas trocadas"
              )}
              <button type="submit" className="full">Salvar fechamento</button>
            </form>
          </section>
        </main>
      )}
    </div>
  );
}

function renderMoneyInput(state, setter, field, label) {
  return (
    <input
      key={field}
      type="number"
      step="0.01"
      value={state[field]}
      placeholder={label}
      onChange={(event) => setter((prev) => ({ ...prev, [field]: event.target.value }))}
      required
    />
  );
}

import React, { useEffect, useMemo, useRef, useState } from "react";
import {
  createUsuario,
  deleteUsuario,
  getAdminDashboard,
  getMe,
  listFiados,
  listOperadores,
  login,
  logout,
  updateUsuario,
  uploadFoto
} from "./api.js";

const tabs = ["Visão Geral", "Caixas", "Fiados", "Relatórios"];

export default function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [authError, setAuthError] = useState("");

  useEffect(() => {
    getMe()
      .then((data) => {
        if (data) {
          setUser(data);
        }
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  async function handleLogin(email, senha) {
    setAuthError("");
    try {
      const data = await login(email, senha);
      setUser(data);
    } catch (err) {
      setAuthError("Login ou senha inválidos.");
    }
  }

  async function handleLogout() {
    try {
      await logout();
    } catch (err) {
      // Ignora erro de logout para permitir sair do app localmente
    }
    setUser(null);
  }

  if (loading) {
    return <div className="loading">Carregando...</div>;
  }

  if (!user) {
    return <LoginView onSubmit={handleLogin} error={authError} />;
  }

  if (user.role === "ADMIN") {
    return <AdminDashboard user={user} onLogout={handleLogout} />;
  }

  return (
    <div className="placeholder">
      <h2>Área em construção</h2>
      <p>As telas de gerente e operador serão adicionadas em seguida.</p>
      <button type="button" onClick={handleLogout}>Sair</button>
    </div>
  );
}

function LoginView({ onSubmit, error }) {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");

  return (
    <div className="login-page">
      <form
        className="login-card"
        onSubmit={(event) => {
          event.preventDefault();
          onSubmit(email, senha);
        }}
      >
        <h1>LotoGestor</h1>
        <p>Faça login para acessar o sistema</p>
        <label>
          Usuário
          <input
            type="email"
            placeholder="Digite seu usuário"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
          />
        </label>
        <label>
          Senha
          <input
            type="password"
            placeholder="Digite sua senha"
            value={senha}
            onChange={(event) => setSenha(event.target.value)}
            required
          />
        </label>
        {error && <span className="error-text">{error}</span>}
        <button type="submit">Entrar</button>
      </form>
    </div>
  );
}

function AdminDashboard({ user, onLogout }) {
  const [activeTab, setActiveTab] = useState("Visão Geral");
  const [dashboard, setDashboard] = useState({
    instantaneas: 0,
    marketplace: 0,
    telesenas: 0,
    totalReceber: 0
  });
  const [operadores, setOperadores] = useState([]);
  const [fiados, setFiados] = useState([]);
  const [fiadoFilters, setFiadoFilters] = useState({
    operadorId: "",
    pessoa: "",
    dataInicio: "",
    dataFim: ""
  });
  const [userModalMode, setUserModalMode] = useState(null);
  const [editingUser, setEditingUser] = useState(null);

  useEffect(() => {
    getAdminDashboard().then(setDashboard).catch(() => {});
    listOperadores().then(setOperadores).catch(() => {});
    listFiados().then(setFiados).catch(() => {});
  }, []);

  async function handleDeleteUser(id) {
    const ok = window.confirm("Deseja excluir este usuario?");
    if (!ok) return;
    try {
      await deleteUsuario(id);
      setOperadores((prev) => prev.filter((item) => item.id !== id));
    } catch (err) {
      // no-op for now
    }
  }

  const fiadoStats = useMemo(() => {
    let pendente = 0;
    let pago = 0;
    fiados.forEach((fiado) => {
      const valor = Number(fiado.valor || 0);
      if (fiado.status === "PAGO") {
        pago += valor;
      } else {
        pendente += valor;
      }
    });
    return {
      pendente,
      pago,
      quantidade: fiados.length
    };
  }, [fiados]);

  async function applyFiadoFilters(nextFilters) {
    const filters = { ...fiadoFilters, ...nextFilters };
    setFiadoFilters(filters);
    const data = await listFiados(filters);
    setFiados(data);
  }

  return (
    <div className="admin-page">
      <header className="admin-header">
        <div>
          <h1>Olá, {user.nomeCompleto.split(" ")[0]}</h1>
          <p className="muted">Painel administrativo</p>
        </div>
        <button type="button" className="icon-button" onClick={onLogout} aria-label="Sair">
          <span className="logout-icon"></span>
        </button>
      </header>

      <section className="summary">
        <div className="summary-title">Valor a Receber da Caixa</div>
        <div className="summary-cards">
          <MetricCard title="Instantâneas" value={dashboard.instantaneas} icon="key" />
          <MetricCard title="MarketPlace" value={dashboard.marketplace} icon="cart" />
          <MetricCard title="Tele-Senas" value={dashboard.telesenas} icon="doc" />
          <MetricCard title="Valor Total a Receber" value={dashboard.totalReceber} icon="money" highlight />
        </div>
      </section>

      <nav className="tabs">
        {tabs.map((tab) => (
          <button
            key={tab}
            type="button"
            className={tab === activeTab ? "active" : ""}
            onClick={() => setActiveTab(tab)}
          >
            {tab}
          </button>
        ))}
      </nav>

      {activeTab === "Visão Geral" && (
        <section className="panel-grid">
          <ChartPanel title="Gráfico 1" />
          <ChartPanel title="Gráfico 2" />
        </section>
      )}

      {activeTab === "Caixas" && (
        <section className="panel-box">
          <div className="panel-header">
            <div className="panel-title">Todos os Caixas</div>
            <button type="button" className="circle-button" onClick={() => setUserModalMode("create")}>
              +
            </button>
          </div>
          <div className="caixas-grid">
            {operadores.map((operador, index) => (
              <div key={operador.id} className="caixa-card">
                <div className="caixa-top">
                  <span>Caixa {index + 1}</span>
                  <div className="card-actions">
                    <button
                      type="button"
                      className="icon-edit"
                      aria-label="Editar caixa"
                      onClick={() => {
                        setEditingUser(operador);
                        setUserModalMode("edit");
                      }}
                    >
                      <span className="edit-icon"></span>
                    </button>
                    <button
                      type="button"
                      className="icon-delete"
                      aria-label="Excluir caixa"
                      onClick={() => handleDeleteUser(operador.id)}
                    >
                      <span className="trash-icon"></span>
                    </button>
                  </div>
                </div>
                <div
                  className={`avatar ${operador.fotoUrl ? "has-photo" : ""}`}
                  style={
                    operador.fotoUrl
                      ? { backgroundImage: `url(${resolveImageUrl(operador.fotoUrl)})` }
                      : undefined
                  }
                >
                  {!operador.fotoUrl && operador.nomeCompleto.charAt(0)}
                </div>
                <p>{operador.nomeCompleto}</p>
              </div>
            ))}
          </div>
        </section>
      )}

      {activeTab === "Fiados" && (
        <section className="panel-box">
          <div className="panel-title">Gestão de Fiados</div>
          <div className="filters">
            <label>
              Operador
              <select
                value={fiadoFilters.operadorId}
                onChange={(event) => applyFiadoFilters({ operadorId: event.target.value })}
              >
                <option value="">Todos</option>
                {operadores.map((operador) => (
                  <option key={operador.id} value={operador.id}>
                    {operador.nomeCompleto}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Pessoa
              <input
                type="text"
                placeholder="Todas"
                value={fiadoFilters.pessoa}
                onChange={(event) => applyFiadoFilters({ pessoa: event.target.value })}
              />
            </label>
            <label>
              Data Início
              <input
                type="date"
                value={fiadoFilters.dataInicio}
                onChange={(event) => applyFiadoFilters({ dataInicio: event.target.value })}
              />
            </label>
            <label>
              Data Fim
              <input
                type="date"
                value={fiadoFilters.dataFim}
                onChange={(event) => applyFiadoFilters({ dataFim: event.target.value })}
              />
            </label>
          </div>

          <div className="fiado-stats">
            <StatCard title="Total Pendente" value={fiadoStats.pendente} tone="danger" icon="money" />
            <StatCard title="Total Pago" value={fiadoStats.pago} tone="success" icon="check" />
            <StatCard title="Quantidade" value={fiadoStats.quantidade} tone="info" icon="users" />
          </div>

          <div className="fiado-table">
            <div className="fiado-row fiado-head">
              <span>Data</span>
              <span>Pessoa</span>
              <span>Operador</span>
              <span>Valor</span>
                <span>Descrição</span>
                <span>Status</span>
                <span>Data do Pagamento</span>
              </div>
            {fiados.map((fiado) => (
              <div key={fiado.id} className="fiado-row">
                <span>{formatDateTime(fiado.data)}</span>
                <span>{fiado.pessoa}</span>
                <span>{fiado.operador || "-"}</span>
                <span className={fiado.status === "PENDENTE" ? "danger" : "success"}>
                  {formatCurrency(fiado.valor)}
                </span>
                <span>{fiado.descricao || "-"}</span>
                <span>
                  <span className={`status-pill ${fiado.status === "PAGO" ? "paid" : "pending"}`}>
                    {fiado.status === "PAGO" ? "Pago" : "Pendente"}
                  </span>
                </span>
                <span>{fiado.dataPagamento ? formatDateTime(fiado.dataPagamento) : "-"}</span>
              </div>
            ))}
          </div>
        </section>
      )}

      {activeTab === "Relatórios" && (
        <section className="panel-box empty">
          <p>Relatórios serão adicionados em breve.</p>
        </section>
      )}

      {userModalMode && (
        <UserModal
          mode={userModalMode}
          initialUser={editingUser}
          onClose={() => {
            setUserModalMode(null);
            setEditingUser(null);
          }}
          onSaved={(saved) => {
            if (userModalMode === "create") {
              setOperadores((prev) => [...prev, saved]);
            } else {
              setOperadores((prev) =>
                prev.map((item) => (item.id === saved.id ? saved : item))
              );
            }
            setUserModalMode(null);
            setEditingUser(null);
          }}
        />
      )}
    </div>
  );
}

function UserModal({ mode, initialUser, onClose, onSaved }) {
  const isEdit = mode === "edit";
  const [form, setForm] = useState({
    nomeCompleto: initialUser?.nomeCompleto || "",
    email: initialUser?.email || "",
    endereco: initialUser?.endereco || "",
    cpf: initialUser?.cpf ? maskCpf(initialUser.cpf) : "",
    senha: "",
    role: initialUser?.role || "OPERADOR",
    fotoUrl: initialUser?.fotoUrl || ""
  });
  const [fotoFile, setFotoFile] = useState(null);
  const [cpfError, setCpfError] = useState("");
  const [emailError, setEmailError] = useState("");
  const [error, setError] = useState("");
  const [cameraOpen, setCameraOpen] = useState(false);
  const [cameraError, setCameraError] = useState("");
  const videoRef = useRef(null);
  const streamRef = useRef(null);

  function handleCpfChange(value) {
    const masked = maskCpf(value);
    setForm((prev) => ({ ...prev, cpf: masked }));
    const digits = masked.replace(/\D/g, "");
    if (digits.length === 11 && !isValidCpf(digits)) {
      setCpfError("CPF inválido.");
    } else {
      setCpfError("");
    }
  }

  function handleEmailChange(value) {
    setForm((prev) => ({ ...prev, email: value }));
    if (value && !isValidEmail(value)) {
      setEmailError("Email inválido.");
    } else {
      setEmailError("");
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    if (emailError) {
      setError("Corrija o email antes de salvar.");
      return;
    }
    const cpfDigits = form.cpf.replace(/\D/g, "");
    if (cpfDigits.length !== 11) {
      setError("CPF incompleto.");
      return;
    }
    if (!isEdit && !form.senha) {
      setError("Senha obrigatória.");
      return;
    }
    if (cpfError) {
      setError("Corrija o CPF antes de salvar.");
      return;
    }
    try {
      let fotoUrl = form.fotoUrl;
      if (fotoFile) {
        const upload = await uploadFoto(fotoFile);
        fotoUrl = upload.url;
      }
      const payload = { ...form, cpf: cpfDigits, fotoUrl };
      const saved = isEdit
        ? await updateUsuario(initialUser.id, payload)
        : await createUsuario(payload);
      onSaved(saved);
    } catch (err) {
      setError(err.message || (isEdit ? "Erro ao atualizar usuário" : "Erro ao criar usuário"));
    }
  }

  async function openCamera() {
    setCameraError("");
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: "environment" }
      });
      streamRef.current = stream;
      setCameraOpen(true);
      if (videoRef.current) {
        videoRef.current.srcObject = stream;
      }
    } catch (err) {
      setCameraError("Nao foi possivel acessar a camera.");
    }
  }

  function closeCamera() {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach((track) => track.stop());
      streamRef.current = null;
    }
    setCameraOpen(false);
  }

  function capturePhoto() {
    if (!videoRef.current) return;
    const video = videoRef.current;
    const canvas = document.createElement("canvas");
    canvas.width = video.videoWidth || 640;
    canvas.height = video.videoHeight || 480;
    const ctx = canvas.getContext("2d");
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
    canvas.toBlob((blob) => {
      if (!blob) return;
      const file = new File([blob], `foto-${Date.now()}.jpg`, { type: "image/jpeg" });
      setFotoFile(file);
      closeCamera();
    }, "image/jpeg", 0.92);
  }

  return (
    <div className="modal-backdrop">
      <div className="modal">
        <div className="modal-header">
        <h3>{isEdit ? "Editar usuário" : "Novo usuário"}</h3>
        <button type="button" className="icon-button" onClick={onClose}>✕</button>
      </div>
      <form className="modal-form" onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Nome completo"
            value={form.nomeCompleto}
            onChange={(event) => setForm((prev) => ({ ...prev, nomeCompleto: event.target.value }))}
            required
          />
          <input
            type="email"
            placeholder="Email"
            value={form.email}
            onChange={(event) => handleEmailChange(event.target.value)}
            required
          />
          <input
            type="text"
            placeholder="Endereço"
            value={form.endereco}
            onChange={(event) => setForm((prev) => ({ ...prev, endereco: event.target.value }))}
            required
          />
          <input
            type="text"
            placeholder="CPF"
            value={form.cpf}
            onChange={(event) => handleCpfChange(event.target.value)}
            maxLength={14}
            required
          />
        <input
          type="password"
          placeholder={isEdit ? "Nova senha (opcional)" : "Senha"}
          value={form.senha}
          onChange={(event) => setForm((prev) => ({ ...prev, senha: event.target.value }))}
          required={!isEdit}
        />
          <select
            value={form.role}
            onChange={(event) => setForm((prev) => ({ ...prev, role: event.target.value }))}
            required
          >
            <option value="GERENTE">Gerente</option>
            <option value="OPERADOR">Operador</option>
          </select>
          <div className="file-field">
            <label className="file-button">
              Escolher arquivo
              <input
                type="file"
                accept="image/*"
                onChange={(event) => setFotoFile(event.target.files[0] || null)}
              />
            </label>
            <button
              type="button"
              className="file-button outline"
              onClick={openCamera}
            >
              Tirar foto
            </button>
            <span className={`file-name ${fotoFile ? "selected" : ""}`}>
              {fotoFile ? fotoFile.name : "Nenhuma imagem selecionada"}
            </span>
          </div>
        {cpfError && <span className="error-text">{cpfError}</span>}
        {emailError && <span className="error-text">{emailError}</span>}
        {error && <span className="error-text">{error}</span>}
        <button type="submit">{isEdit ? "Salvar alterações" : "Criar usuário"}</button>
      </form>
    </div>

      {cameraOpen && (
        <div className="modal-backdrop">
          <div className="modal camera-modal">
            <div className="modal-header">
              <h3>Capturar foto</h3>
              <button type="button" className="icon-button" onClick={closeCamera}>✕</button>
            </div>
            <video ref={videoRef} autoPlay playsInline className="camera-preview" />
            <div className="camera-actions">
              <button type="button" className="file-button outline" onClick={closeCamera}>
                Cancelar
              </button>
              <button type="button" className="file-button" onClick={capturePhoto}>
                Usar foto
              </button>
            </div>
          </div>
        </div>
      )}
      {cameraError && <span className="error-text">{cameraError}</span>}
    </div>
  );
}

function MetricCard({ title, value, icon, highlight }) {
  return (
    <div className={`metric-card ${highlight ? "highlight" : ""}`}>
      <div>
        <span className="metric-title">{title}</span>
        <strong>{formatCurrency(value)}</strong>
      </div>
      <span className={`metric-icon ${icon}`}></span>
    </div>
  );
}

function ChartPanel({ title }) {
  return (
    <div className="chart-panel">
      <div className="chart-filter">⎇</div>
      <div className="chart-title">{title}</div>
    </div>
  );
}

function StatCard({ title, value, tone, icon }) {
  return (
    <div className={`stat-card ${tone}`}>
      <div>
        <span>{title}</span>
        <strong>{tone === "info" ? value : formatCurrency(value)}</strong>
      </div>
      <span className={`stat-icon ${icon}`}></span>
    </div>
  );
}

function formatCurrency(value) {
  const number = Number(value || 0);
  return number.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

function formatDateTime(value) {
  if (!value) return "";
  return new Date(value).toLocaleString("pt-BR");
}

function isValidEmail(value) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
}

function resolveImageUrl(url) {
  if (!url) return url;
  if (/^https?:\/\//i.test(url)) return url;
  const base = (import.meta.env.VITE_API_BASE || "http://localhost:8080/api").replace(/\/api\/?$/, "");
  return `${base}${url.startsWith("/") ? "" : "/"}${url}`;
}

function maskCpf(value) {
  const digits = value.replace(/\D/g, "").slice(0, 11);
  const parts = [];
  if (digits.length > 3) {
    parts.push(digits.slice(0, 3));
    if (digits.length > 6) {
      parts.push(digits.slice(3, 6));
      if (digits.length > 9) {
        parts.push(digits.slice(6, 9));
        return `${parts[0]}.${parts[1]}.${parts[2]}-${digits.slice(9)}`;
      }
      return `${parts[0]}.${parts[1]}.${digits.slice(6)}`;
    }
    return `${parts[0]}.${digits.slice(3)}`;
  }
  return digits;
}

function isValidCpf(cpf) {
  if (!cpf || cpf.length !== 11) return false;
  if (/^(\d)\1{10}$/.test(cpf)) return false;

  let sum = 0;
  for (let i = 0; i < 9; i += 1) {
    sum += Number(cpf[i]) * (10 - i);
  }
  let first = (sum * 10) % 11;
  if (first === 10) first = 0;
  if (first !== Number(cpf[9])) return false;

  sum = 0;
  for (let i = 0; i < 10; i += 1) {
    sum += Number(cpf[i]) * (11 - i);
  }
  let second = (sum * 10) % 11;
  if (second === 10) second = 0;
  return second === Number(cpf[10]);
}

const API_BASE = import.meta.env.VITE_API_BASE || "http://localhost:8080/api";

async function apiFetch(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    credentials: "include",
    headers: {
      ...(options.headers || {})
    },
    ...options
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Erro ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

export function login(email, senha) {
  return apiFetch("/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, senha })
  });
}

export function logout() {
  return apiFetch("/auth/logout", {
    method: "POST"
  });
}

export function getMe() {
  return apiFetch("/auth/me");
}

export function getAdminDashboard() {
  return apiFetch("/admin/dashboard");
}

export function listOperadores() {
  return apiFetch("/admin/operadores");
}

export function listFiados(params = {}) {
  const search = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      search.set(key, value);
    }
  });
  const query = search.toString();
  return apiFetch(`/admin/fiados${query ? `?${query}` : ""}`);
}

export function createUsuario(payload) {
  return apiFetch("/admin/usuarios", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });
}

export function updateUsuario(id, payload) {
  return apiFetch(`/admin/usuarios/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });
}

export function deleteUsuario(id) {
  return apiFetch(`/admin/usuarios/${id}`, {
    method: "DELETE"
  });
}

export async function uploadFoto(file) {
  const form = new FormData();
  form.append("file", file);
  const response = await fetch(`${API_BASE}/admin/upload`, {
    method: "POST",
    credentials: "include",
    body: form
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Erro ${response.status}`);
  }
  return response.json();
}

const API_BASE = import.meta.env.VITE_API_BASE || "http://localhost:8080/api";

async function apiFetch(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      "Content-Type": "application/json",
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

export function getDashboard() {
  return apiFetch("/dashboard");
}

export function createPessoa(data) {
  return apiFetch("/pessoas", {
    method: "POST",
    body: JSON.stringify(data)
  });
}

export function createFiado(data) {
  return apiFetch("/fiados", {
    method: "POST",
    body: JSON.stringify(data)
  });
}

export function deleteFiado(id) {
  return apiFetch(`/fiados/${id}`, {
    method: "DELETE"
  });
}

export function createAbertura(data) {
  return apiFetch("/aberturas", {
    method: "POST",
    body: JSON.stringify(data)
  });
}

export function createFechamento(data) {
  return apiFetch("/fechamentos", {
    method: "POST",
    body: JSON.stringify(data)
  });
}

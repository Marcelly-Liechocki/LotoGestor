# LotoGestor

## Visao geral

- Backend: Spring Boot (API em `/api`)
- Frontend: React + Vite (pasta `frontend`)
- Login inicial (admin): `admin@lotogestor.com` / `123456`

## Como rodar

### Backend

```bash
./gradlew bootRun
```

API local: `http://localhost:8080/api`

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend local: `http://localhost:5173`

Se precisar apontar para outra URL, use `VITE_API_BASE`:

```bash
VITE_API_BASE=http://localhost:8080/api npm run dev
```

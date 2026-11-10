<div align="center">

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="frontend-app/src/assets/logo-dark.svg">
  <img src="frontend-app/src/assets/logo.svg" alt="Stow logo" width="96">
</picture>

# Packlist · *Stow*

**Never forget your toothbrush again.**
Build reusable packing templates, spin them into lists for every trip, and tick things off as you go.

![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1-6DB33F?logo=springboot&logoColor=white)
![Vue](https://img.shields.io/badge/Vue-3.5-4FC08D?logo=vuedotjs&logoColor=white)
![Vuetify](https://img.shields.io/badge/Vuetify-4-1867C0?logo=vuetify&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-6-3178C6?logo=typescript&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-4169E1?logo=postgresql&logoColor=white)
![Playwright](https://img.shields.io/badge/Playwright-E2E-2EAD33?logo=playwright&logoColor=white)
![pnpm](https://img.shields.io/badge/pnpm-workspace-F69220?logo=pnpm&logoColor=white)

</div>

---

## ✨ What's inside

Packlist is a small, api-first **microservice playground**: two Spring Boot services, a Vue 3 SPA, and
OpenAPI specs that generate type-safe clients for both Java and TypeScript.

|                              |                                                                                                                                           |
|------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| 🔐 **Auth that scales out**  | `user-service` issues RS256-signed JWTs and exposes its public key — other services verify tokens on their own, no shared session.        |
| 📜 **Contract-first APIs**   | OpenAPI specs live in `*-api` modules. Server interfaces, a Java client and TypeScript clients are generated from them.                   |
| 🧱 **Shared backend core**   | `backend-common` bundles JWT decoding, security config and the current-user resolver behind a single `@EnablePacklistCommon`.             |
| 🎨 **Modern frontend**       | Vue 3 + Vuetify 4, Pinia Colada for server state, VeeValidate + Zod for forms, vue-i18n for copy.                                         |
| 🧪 **Tested on every layer** | JUnit + Mockito, Testcontainers & WireMock integration tests, Vitest + Testing Library, and Playwright E2E against the real Docker stack. |

---

## 🗺️ Architecture

```
                       ┌──────────────────────────────┐
     Browser  ───────▶ │  Caddy  (frontend container) │
                       │  serves SPA + reverse proxy  │
                       └───────┬──────────────┬───────┘
          /api/auth, /api/users│              │/api/lists, /api/templates
                               ▼              ▼
                     ┌──────────────┐   ┌──────────────┐
                     │ user-service │   │ list-service │
                     │    :8881     │◀──│    :8871     │  verifies JWTs
                     └──────┬───────┘   └──────┬───────┘  with user-service's
                            ▼                  ▼          public key
                     ┌──────────────┐   ┌──────────────┐
                     │ postgres-user│   │ postgres-list│
                     └──────────────┘   └──────────────┘
```

Each service owns its database; schemas are managed with **Flyway**.

---

## 📦 Repository layout

```
packlist/
├── backend-common/     Shared Spring security & JWT plumbing
├── user-api/           OpenAPI spec for users & auth   → generated server interfaces
├── user-client-java/   Java client generated from user-api
├── user-client-ts/     @packlist/users-api  (TypeScript client)
├── user-service/       Registration, login, JWT issuing          (Spring Boot)
├── list-api/           OpenAPI spec for lists & templates
├── list-client-ts/     @packlist/lists-api  (TypeScript client)
├── list-service/       Packing templates & lists                  (Spring Boot)
├── frontend-app/       The Stow web app                           (Vue 3 + Vite)
├── e2e/                Playwright end-to-end tests
├── infra/caddy/        Caddyfile for the production-style frontend container
├── compose.yml         Local databases for development
└── compose-e2e.yml     Full stack for E2E runs
```

---

## 🚀 Getting started

### Prerequisites

- **JDK 25**
- **Node.js** 22.18+ or 24.12+ with **pnpm**
- **Docker** (databases, Testcontainers, E2E)
- **OpenSSL** (for generating JWT keys)

### 1 · Configure & start the databases

```bash
cp .env.example .env          # defaults work out of the box — tweak if you like
docker compose up -d          # postgres-user on :8889, postgres-list on :8879
```

### 2 · Generate a JWT key pair

```bash
./generate-keys.sh            # writes user-service/src/main/resources/keys/{private,public}.pem
```

> The `keys/` folder is git-ignored — keep it that way. 🔑

### 3 · Build & run the backend

```bash
./mvnw install -DskipTests

./mvnw -pl user-service spring-boot:run      # → http://localhost:8881
./mvnw -pl list-service spring-boot:run      # → http://localhost:8871
```

### 4 · Run the frontend

```bash
pnpm install
pnpm --filter "@packlist/*" generate         # build the TypeScript API clients
pnpm --filter frontend-app dev               # → http://localhost:5173
```

The Vite dev server proxies `/api/*` to the right service, so there's no CORS fiddling.

---

## 🧪 Testing

| What                                    | Command                                                                     |
|-----------------------------------------|-----------------------------------------------------------------------------|
| Backend unit + integration tests        | `./mvnw verify`                                                             |
| Frontend unit tests                     | `pnpm --filter frontend-app test`                                           |
| Frontend type check & lint              | `pnpm --filter frontend-app type-check` · `pnpm --filter frontend-app lint` |
| End-to-end (spins up `compose-e2e.yml`) | `pnpm --filter e2e test`                                                    |
| E2E in interactive UI mode              | `pnpm --filter e2e test:ui`                                                 |

Integration tests start throwaway PostgreSQL containers via **Testcontainers**, and `list-service` talks to a
**WireMock**-stubbed `user-service` — so Docker needs to be running.

---

## 🔌 API at a glance

| Service      | Endpoint                  | Purpose                        |
|--------------|---------------------------|--------------------------------|
| user-service | `POST /api/auth/register` | Create an account              |
| user-service | `POST /api/auth/login`    | Exchange credentials for a JWT |
| user-service | `GET  /api/users/me`      | Who am I?                      |
| list-service | `/api/templates/`         | Reusable packing templates     |
| list-service | `/api/lists/`             | Concrete packing lists         |

Full contracts: [`user-api/src/main/resources/users-api.yml`](user-api/src/main/resources/users-api.yml) ·
[`list-api/src/main/resources/lists-api.yml`](list-api/src/main/resources/lists-api.yml)

Errors follow [RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457).

---

## 🛣️ Roadmap

- [x] Registration & login with JWT
- [x] Shared security module for all services
- [x] Playwright E2E against the Docker stack
- [ ] Packing templates (in progress)
- [ ] Packing lists created from templates
- [ ] 2FA and passkeys/login with google
- [ ] Sharing lists with other users

---

<div align="center">

Built with ☕ and a slightly overpacked suitcase by **David Schwarz**.

</div>

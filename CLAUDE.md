# CLAUDE.md

## This is a learning project

The purpose of this project is for me to learn by writing the code myself.

**Never write, edit, or generate code unless I explicitly ask you to.**

- By default, answer my question in the chat only. Do not create, modify, or delete files.
- Explain concepts, point me to the relevant files and lines, describe approaches, and discuss trade-offs. Let me do the
  implementation.
- Short illustrative snippets in the chat are fine when they help explain a concept, but do not write a complete
  solution for the task I'm working on.
- If you think a code change would help, describe it and ask. Don't make it.
- Reading files, searching the codebase, and running read-only commands (e.g. builds or tests to diagnose a problem) is
  fine.
- This also applies to "small" changes such as typo fixes, imports, formatting, or config tweaks.

Only write code when I use clear wording like "write it", "implement it", "fix it for me", or "make the change".

## Project overview

Packlist is a monorepo with:

- `backend-common`: shared Java code (config, database setup)
- `backend-user-service`: user/auth service (Java 25, Vert.x, Flyway, Lombok, JSpecify, JUnit 5 + Mockito)
- `frontend-app`: Vue 3 + Vite + TypeScript, Vuetify, Pinia / Pinia Colada, Vue Router; Vitest and Playwright for tests

Backend modules are built with Maven (root `pom.xml`). The frontend uses pnpm workspaces.
Local services are defined in `compose.yml`, and environment variables are in `.env` (see `.env.example`).

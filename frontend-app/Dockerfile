FROM node:24.18-alpine AS build

WORKDIR /build

RUN corepack enable

ENV PNPM_HOME=/pnpm
ENV PATH=$PNPM_HOME:$PATH

COPY package.json pnpm-workspace.yaml pnpm-lock.yaml ./

RUN --mount=type=cache,id=pnpm,target=/pnpm/store pnpm fetch

COPY user-api/ ./user-api/
COPY user-client-ts/ ./user-client-ts/
COPY frontend-app/ ./frontend-app/

RUN --mount=type=cache,id=pnpm,target=/pnpm/store pnpm install --frozen-lockfile --offline --filter "frontend-app..."
RUN pnpm --filter "frontend-app..." build

FROM caddy:2-alpine

COPY infra/caddy/Caddyfile /etc/caddy/Caddyfile
COPY --from=build /build/frontend-app/dist /srv/frontend
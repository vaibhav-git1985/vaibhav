FROM node:20-alpine AS deps
WORKDIR /app
COPY package.json ./
RUN npm install

FROM node:20-alpine AS build
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
ENV NEXT_TELEMETRY_DISABLED=1
ENV NEXTAUTH_SECRET=build-time-placeholder-secret-min-32-chars-long
ENV NEXTAUTH_URL=http://localhost:3000
ENV OKTA_OAUTH2_CLIENT_ID=
ENV OKTA_OAUTH2_CLIENT_SECRET=
ENV OKTA_OAUTH2_ISSUER=
ENV ALLOW_DEV_LOGIN=true
ENV NODE_ENV=production
RUN npm run build

FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production
COPY package.json ./
RUN npm install --omit=dev
COPY --from=build /app/.next ./.next
COPY --from=build /app/public ./public
EXPOSE 3000
CMD ["npm", "run", "start"]

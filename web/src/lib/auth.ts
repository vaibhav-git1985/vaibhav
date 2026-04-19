import type { NextAuthOptions } from "next-auth";
import CredentialsProvider from "next-auth/providers/credentials";
import FacebookProvider from "next-auth/providers/facebook";
import OktaProvider from "next-auth/providers/okta";

const gatewayUrl =
  process.env.GATEWAY_URL || process.env.NEXT_PUBLIC_GATEWAY_URL || "http://localhost:9292";

const hasOkta =
  Boolean(process.env.OKTA_OAUTH2_CLIENT_ID) &&
  Boolean(process.env.OKTA_OAUTH2_CLIENT_SECRET) &&
  Boolean(process.env.OKTA_OAUTH2_ISSUER);

const hasFacebook =
  Boolean(process.env.FACEBOOK_CLIENT_ID) && Boolean(process.env.FACEBOOK_CLIENT_SECRET);

const allowDevLogin =
  process.env.ALLOW_DEV_LOGIN === "true" || process.env.NODE_ENV !== "production";

function buildProviders(): NextAuthOptions["providers"] {
  const list: NextAuthOptions["providers"] = [];
  if (hasFacebook) {
    list.push(
      FacebookProvider({
        clientId: process.env.FACEBOOK_CLIENT_ID!,
        clientSecret: process.env.FACEBOOK_CLIENT_SECRET!,
      }),
    );
  }
  if (hasOkta) {
    list.push(
      OktaProvider({
        clientId: process.env.OKTA_OAUTH2_CLIENT_ID!,
        clientSecret: process.env.OKTA_OAUTH2_CLIENT_SECRET!,
        issuer: process.env.OKTA_OAUTH2_ISSUER!,
      }),
    );
  }
  if (list.length === 0 || allowDevLogin) {
    list.push(
      CredentialsProvider({
        id: "dev-credentials",
        name: "Development sign-in",
        credentials: {
          sub: { label: "User id (like OIDC sub)", type: "text", placeholder: "dev-user-1" },
        },
        async authorize(credentials) {
          if (process.env.NODE_ENV === "production" && process.env.ALLOW_DEV_LOGIN !== "true") {
            return null;
          }
          const sub = credentials?.sub?.trim() || "dev-user-1";
          return {
            id: sub,
            email: `${sub}@example.local`,
            name: sub,
          };
        },
      }),
    );
  }
  return list;
}

export const authOptions: NextAuthOptions = {
  providers: buildProviders(),
  pages: {
    signIn: "/auth/signin",
  },
  session: { strategy: "jwt" },
  callbacks: {
    async jwt({ token, account, profile, user }) {
      if (account?.access_token) {
        token.accessToken = account.access_token;
      }
      if (profile) {
        const p = profile as { sub?: string; id?: string | number };
        if (p.sub) {
          token.sub = p.sub;
        } else if (p.id != null) {
          token.sub = String(p.id);
        }
      }
      if (user?.id) {
        token.sub = user.id;
      }
      return token;
    },
    async session({ session, token }) {
      if (token.accessToken) {
        session.accessToken = token.accessToken as string;
      }
      if (token.sub && session.user) {
        session.user.id = token.sub;
      }
      return session;
    },
  },
  events: {
    async signIn({ profile, user }) {
      const p = profile as { sub?: string; id?: string | number } | undefined;
      const sub = p?.sub || (p?.id != null ? String(p.id) : undefined) || user?.id;
      if (!sub) return;
      const email = (profile as { email?: string } | undefined)?.email || user?.email || undefined;
      const username =
        (profile as { preferred_username?: string } | undefined)?.preferred_username ||
        email ||
        sub;
      try {
        await fetch(`${gatewayUrl}/users/sync`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "X-User-Sub": sub,
          },
          body: JSON.stringify({ email, username }),
        });
      } catch {
        /* non-fatal */
      }
    },
  },
};

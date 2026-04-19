"use client";

import { getProviders, signIn } from "next-auth/react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { Suspense, useEffect, useState } from "react";

type ProviderList = Awaited<ReturnType<typeof getProviders>>;

function SignInContent() {
  const searchParams = useSearchParams();
  const callbackUrl = searchParams.get("callbackUrl") || "/";
  const [providers, setProviders] = useState<ProviderList>(null);
  const [devSub, setDevSub] = useState("dev-user-1");
  const [busy, setBusy] = useState<string | null>(null);

  useEffect(() => {
    getProviders().then(setProviders);
  }, []);

  async function oauth(providerId: "facebook" | "okta") {
    setBusy(providerId);
    try {
      await signIn(providerId, { callbackUrl });
    } finally {
      setBusy(null);
    }
  }

  async function devSignIn() {
    setBusy("dev-credentials");
    try {
      await signIn("dev-credentials", {
        callbackUrl,
        redirect: true,
        sub: devSub,
      });
    } finally {
      setBusy(null);
    }
  }

  return (
    <main className="page page--narrow">
      <div className="auth-card">
        <h1>Sign in</h1>
        <p className="muted">
          Sign in with Facebook (Meta), with Okta (you can wire Facebook as an IdP inside Okta), or use the
          development account when it is enabled.
        </p>

        <div className="auth-actions">
          {providers?.facebook ? (
            <button
              type="button"
              className="btn btn--facebook"
              disabled={!!busy}
              onClick={() => oauth("facebook")}
            >
              {busy === "facebook" ? "Redirecting…" : "Continue with Facebook"}
            </button>
          ) : null}

          {providers?.okta ? (
            <button type="button" className="btn btn--okta" disabled={!!busy} onClick={() => oauth("okta")}>
              {busy === "okta" ? "Redirecting…" : "Continue with Okta"}
            </button>
          ) : null}

          {providers?.["dev-credentials"] ? (
            <div className="auth-dev">
              <label className="field">
                <span>Dev user id</span>
                <input value={devSub} onChange={(e) => setDevSub(e.target.value)} placeholder="dev-user-1" />
              </label>
              <button type="button" className="btn btn--secondary" disabled={!!busy} onClick={() => void devSignIn()}>
                {busy === "dev-credentials" ? "Signing in…" : "Sign in (development)"}
              </button>
            </div>
          ) : null}
        </div>

        {!providers ? <p className="muted">Loading sign-in options…</p> : null}

        {providers && !providers.facebook && !providers.okta && !providers["dev-credentials"] ? (
          <p className="error-banner">
            No providers configured. Set FACEBOOK_CLIENT_ID / FACEBOOK_CLIENT_SECRET, OKTA_OAUTH2_* variables, or
            enable development sign-in.
          </p>
        ) : null}

        <p className="small muted">
          Facebook app: add <code>http://localhost:3000/api/auth/callback/facebook</code> under Valid OAuth Redirect
          URIs. For “Facebook via Okta”, configure Facebook as an Identity Provider in your Okta admin UI.
        </p>

        <Link href="/" className="link-back">
          ← Back to store
        </Link>
      </div>
    </main>
  );
}

export default function SignInPage() {
  return (
    <Suspense fallback={<main className="page"><p>Loading…</p></main>}>
      <SignInContent />
    </Suspense>
  );
}

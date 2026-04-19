"use client";

import Link from "next/link";
import { useSession } from "next-auth/react";
import { useSearchParams } from "next/navigation";
import { Suspense, useEffect, useState } from "react";
import { useCart } from "@/context/CartContext";

function SuccessInner() {
  const searchParams = useSearchParams();
  const sessionId = searchParams.get("session_id");
  const { data: session } = useSession();
  const { clearCart } = useCart();
  const [msg, setMsg] = useState("Confirming payment…");
  const [err, setErr] = useState<string | null>(null);

  useEffect(() => {
    if (!sessionId || !session) {
      setMsg("Missing session or not signed in.");
      return;
    }
    (async () => {
      try {
        const res = await fetch("/api/payment/verify-session", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ sessionId }),
        });
        if (!res.ok) {
          const t = await res.text();
          throw new Error(t || "Verify failed");
        }
        setMsg("Payment confirmed. Delivery job queued.");
        clearCart();
      } catch (e: unknown) {
        setErr(e instanceof Error ? e.message : "Error");
        setMsg("");
      }
    })();
  }, [sessionId, session, clearCart]);

  return (
    <main className="page">
      <div className="page-header">
        <h1>Thank you</h1>
      </div>
      {sessionId ? <p className="muted small">Stripe session: {sessionId}</p> : null}
      {err ? <p className="error-banner">{err}</p> : <p>{msg}</p>}
      <p style={{ marginTop: "1.5rem" }}>
        <Link href="/account/orders" className="btn btn--secondary">
          View orders
        </Link>{" "}
        <Link href="/" className="btn btn--ghost">
          Home
        </Link>
      </p>
    </main>
  );
}

export default function CheckoutSuccessPage() {
  return (
    <Suspense
      fallback={
        <main className="page">
          <p>Loading…</p>
        </main>
      }
    >
      <SuccessInner />
    </Suspense>
  );
}

"use client";

import Link from "next/link";
import { useSession } from "next-auth/react";
import { useState } from "react";
import { useCart } from "@/context/CartContext";

export default function CheckoutPage() {
  const { data: session, status } = useSession();
  const { lines, subtotal } = useCart();
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function pay() {
    setError(null);
    setBusy(true);
    try {
      if (!session) {
        setError("Please sign in to place an order.");
        setBusy(false);
        return;
      }
      if (lines.length === 0) {
        setError("Your cart is empty.");
        setBusy(false);
        return;
      }
      const orderRes = await fetch("/api/orders", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          items: lines.map((l) => ({
            productId: l.productId,
            productName: l.productName,
            quantity: l.quantity,
            unitPrice: l.unitPrice,
          })),
        }),
      });
      if (!orderRes.ok) {
        const t = await orderRes.text();
        throw new Error(t || "Order failed");
      }
      const order = await orderRes.json();
      const payRes = await fetch("/api/payment/checkout-session", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ orderId: order.id }),
      });
      if (!payRes.ok) {
        const t = await payRes.text();
        throw new Error(t || "Payment session failed");
      }
      const pay = await payRes.json();
      if (!pay.checkoutUrl) throw new Error("No checkout URL from payment service");
      window.location.href = pay.checkoutUrl;
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "Error");
      setBusy(false);
    }
  }

  if (status === "loading") {
    return (
      <main className="page">
        <p className="muted">Loading session…</p>
      </main>
    );
  }

  return (
    <main className="page">
      <div className="page-header">
        <h1>Checkout</h1>
        <p className="muted">Pay with Stripe (test mode when configured).</p>
      </div>

      {!session && (
        <div className="auth-card" style={{ marginBottom: "1.25rem" }}>
          <p style={{ marginTop: 0 }}>Sign in to place an order.</p>
          <Link href="/auth/signin?callbackUrl=/checkout" className="btn btn--primary">
            Sign in
          </Link>
        </div>
      )}

      {lines.length === 0 ? (
        <div className="empty-state">
          <p>Your cart is empty.</p>
          <Link href="/products" className="btn btn--primary">
            Browse catalog
          </Link>
        </div>
      ) : (
        <>
          <ul className="cart-list">
            {lines.map((l) => (
              <li key={l.productId} className="cart-row">
                <div className="cart-row__info">
                  <strong>{l.productName}</strong>
                  <span className="muted">${l.unitPrice.toFixed(2)} × {l.quantity}</span>
                </div>
                <div className="cart-row__line">${(l.quantity * l.unitPrice).toFixed(2)}</div>
              </li>
            ))}
          </ul>
          <div className="cart-summary" style={{ marginBottom: "1rem" }}>
            <p className="cart-summary__total">
              Total <strong>${subtotal.toFixed(2)}</strong>
            </p>
            <Link href="/cart" className="btn btn--ghost btn--sm">
              Edit cart
            </Link>
          </div>
          <p>
            <button type="button" className="btn btn--primary" disabled={busy || !session} onClick={() => pay()}>
              {busy ? "Redirecting…" : "Pay with Stripe"}
            </button>
          </p>
        </>
      )}
      {error && <p className="error-banner">{error}</p>}
    </main>
  );
}

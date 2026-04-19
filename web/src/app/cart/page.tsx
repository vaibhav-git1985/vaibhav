"use client";

import Link from "next/link";
import { useCart } from "@/context/CartContext";

export default function CartPage() {
  const { lines, subtotal, setQuantity, removeLine } = useCart();

  return (
    <main className="page">
      <div className="page-header">
        <h1>Your cart</h1>
        <p className="muted">Review items before checkout.</p>
      </div>

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
                  <span className="muted">${l.unitPrice.toFixed(2)} each</span>
                </div>
                <div className="cart-row__controls">
                  <input
                    type="number"
                    min={1}
                    className="cart-row__qty"
                    value={l.quantity}
                    onChange={(e) => setQuantity(l.productId, Number(e.target.value) || 1)}
                    aria-label={`Quantity for ${l.productName}`}
                  />
                  <button type="button" className="btn btn--ghost btn--sm" onClick={() => removeLine(l.productId)}>
                    Remove
                  </button>
                </div>
                <div className="cart-row__line">${(l.quantity * l.unitPrice).toFixed(2)}</div>
              </li>
            ))}
          </ul>
          <div className="cart-summary">
            <p className="cart-summary__total">
              Subtotal <strong>${subtotal.toFixed(2)}</strong>
            </p>
            <div className="cart-summary__actions">
              <Link href="/products" className="btn btn--ghost">
                Continue shopping
              </Link>
              <Link href="/checkout" className="btn btn--primary">
                Proceed to checkout
              </Link>
            </div>
          </div>
        </>
      )}
    </main>
  );
}

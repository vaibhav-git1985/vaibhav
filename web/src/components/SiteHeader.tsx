"use client";

import Link from "next/link";
import { useSession } from "next-auth/react";
import { useCart } from "@/context/CartContext";

export function SiteHeader() {
  const { data: session, status } = useSession();
  const { itemCount } = useCart();

  return (
    <header className="site-header">
      <div className="site-header__inner">
        <Link href="/" className="site-logo">
          Storefront
        </Link>
        <nav className="site-nav">
          <Link href="/products">Catalog</Link>
          <Link href="/cart" className="site-nav__cart">
            Cart
            {itemCount > 0 ? <span className="cart-badge">{itemCount}</span> : null}
          </Link>
          <Link href="/checkout">Checkout</Link>
          <Link href="/account/orders">Orders</Link>
          {status === "loading" ? (
            <span className="muted">…</span>
          ) : session ? (
            <>
              <span className="site-nav__user muted">{session.user?.name || session.user?.email}</span>
              <Link href="/api/auth/signout">Sign out</Link>
            </>
          ) : (
            <Link href="/auth/signin" className="btn btn--sm btn--primary">
              Sign in
            </Link>
          )}
        </nav>
      </div>
    </header>
  );
}

"use client";

import Link from "next/link";
import { AddToCartButton } from "@/components/AddToCartButton";

export type ProductCard = { id: string; name: string; qty: number; price: number };

export function ProductGrid({ products }: { products: ProductCard[] }) {
  return (
    <div className="product-grid">
      {products.map((p) => (
        <article key={p.id} className="product-card">
          <div className="product-card__body">
            <h2 className="product-card__title">
              <Link href={`/products/${p.id}`}>{p.name}</Link>
            </h2>
            <p className="product-card__meta">
              <span className="price">${p.price.toFixed(2)}</span>
              <span className="muted">{p.qty} in stock</span>
            </p>
          </div>
          <div className="product-card__actions">
            <Link href={`/products/${p.id}`} className="btn btn--ghost btn--sm">
              Details
            </Link>
            <AddToCartButton
              productId={p.id}
              productName={p.name}
              price={p.price}
              maxQty={p.qty}
              size="sm"
            />
          </div>
        </article>
      ))}
    </div>
  );
}

"use client";

import { AddToCartButton } from "@/components/AddToCartButton";

export function ProductDetailActions({
  id,
  name,
  price,
  qty,
}: {
  id: string;
  name: string;
  price: number;
  qty: number;
}) {
  return (
    <div className="product-detail__buy">
      <AddToCartButton productId={id} productName={name} price={price} maxQty={qty} />
      <p className="muted small">Ships after checkout. Mock delivery in this demo.</p>
    </div>
  );
}

"use client";

import { useState } from "react";
import { useCart } from "@/context/CartContext";

type Props = {
  productId: string;
  productName: string;
  price: number;
  maxQty?: number;
  size?: "sm" | "md";
};

export function AddToCartButton({
  productId,
  productName,
  price,
  maxQty = 99,
  size = "md",
}: Props) {
  const { addItem } = useCart();
  const [qty, setQty] = useState(1);
  const [flash, setFlash] = useState(false);

  function add() {
    const q = Math.min(Math.max(1, qty), maxQty);
    addItem({ id: productId, name: productName, price }, q);
    setFlash(true);
    setTimeout(() => setFlash(false), 1200);
  }

  return (
    <div className={`add-to-cart add-to-cart--${size}`}>
      <label className="add-to-cart__qty">
        <span className="sr-only">Quantity</span>
        <input
          type="number"
          min={1}
          max={maxQty}
          value={qty}
          onChange={(e) => setQty(Number(e.target.value) || 1)}
          aria-label="Quantity"
        />
      </label>
      <button type="button" className="btn btn--primary" onClick={add}>
        {flash ? "Added ✓" : "Add to cart"}
      </button>
    </div>
  );
}

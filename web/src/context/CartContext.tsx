"use client";

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import {
  CART_STORAGE_KEY,
  type CartLine,
  readCartFromStorage,
  writeCartToStorage,
} from "@/lib/cart";

type CartContextValue = {
  lines: CartLine[];
  itemCount: number;
  subtotal: number;
  addItem: (product: { id: string; name: string; price: number }, quantity?: number) => void;
  setQuantity: (productId: string, quantity: number) => void;
  removeLine: (productId: string) => void;
  clearCart: () => void;
};

const CartContext = createContext<CartContextValue | null>(null);

export function CartProvider({ children }: { children: ReactNode }) {
  const [lines, setLines] = useState<CartLine[]>([]);
  const [ready, setReady] = useState(false);

  useEffect(() => {
    setLines(readCartFromStorage());
    setReady(true);
  }, []);

  useEffect(() => {
    if (!ready) return;
    writeCartToStorage(lines);
  }, [lines, ready]);

  const addItem = useCallback(
    (product: { id: string; name: string; price: number }, quantity = 1) => {
      setLines((prev) => {
        const idx = prev.findIndex((l) => l.productId === product.id);
        if (idx >= 0) {
          const next = [...prev];
          next[idx] = {
            ...next[idx],
            quantity: next[idx].quantity + quantity,
          };
          return next;
        }
        return [
          ...prev,
          {
            productId: product.id,
            productName: product.name,
            quantity,
            unitPrice: product.price,
          },
        ];
      });
    },
    [],
  );

  const setQuantity = useCallback((productId: string, quantity: number) => {
    if (quantity < 1) {
      setLines((prev) => prev.filter((l) => l.productId !== productId));
      return;
    }
    setLines((prev) =>
      prev.map((l) => (l.productId === productId ? { ...l, quantity } : l)),
    );
  }, []);

  const removeLine = useCallback((productId: string) => {
    setLines((prev) => prev.filter((l) => l.productId !== productId));
  }, []);

  const clearCart = useCallback(() => setLines([]), []);

  const value = useMemo<CartContextValue>(() => {
    const itemCount = lines.reduce((s, l) => s + l.quantity, 0);
    const subtotal = lines.reduce((s, l) => s + l.quantity * l.unitPrice, 0);
    return {
      lines,
      itemCount,
      subtotal,
      addItem,
      setQuantity,
      removeLine,
      clearCart,
    };
  }, [lines, addItem, setQuantity, removeLine, clearCart]);

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart() {
  const ctx = useContext(CartContext);
  if (!ctx) throw new Error("useCart must be used within CartProvider");
  return ctx;
}

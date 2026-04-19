"use client";

import { SessionProvider } from "next-auth/react";
import type { ReactNode } from "react";
import { CartProvider } from "@/context/CartContext";
import { SiteHeader } from "@/components/SiteHeader";

export function Providers({ children }: { children: ReactNode }) {
  return (
    <SessionProvider>
      <CartProvider>
        <SiteHeader />
        <div className="app-main">{children}</div>
      </CartProvider>
    </SessionProvider>
  );
}

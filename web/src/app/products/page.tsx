import Link from "next/link";
import { serverGatewayBase } from "@/lib/gateway";
import { ProductGrid, type ProductCard } from "./ProductGrid";

async function loadProducts(): Promise<ProductCard[]> {
  const base = serverGatewayBase();
  try {
    const res = await fetch(`${base}/products`, { cache: "no-store" });
    if (!res.ok) return [];
    return res.json();
  } catch {
    return [];
  }
}

export default async function ProductsPage() {
  const products = await loadProducts();
  return (
    <main className="page">
      <div className="page-header">
        <h1>Catalog</h1>
        <p className="muted">Pick products and add them to your cart.</p>
        <Link href="/" className="muted small">
          ← Home
        </Link>
      </div>
      {products.length === 0 ? (
        <div className="empty-state">
          <p>No products yet.</p>
          <p className="muted small">Run ./scripts/seed-demo-data.sh with the stack running, or POST to /products.</p>
        </div>
      ) : (
        <ProductGrid products={products} />
      )}
    </main>
  );
}

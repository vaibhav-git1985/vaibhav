import Link from "next/link";
import { serverGatewayBase } from "@/lib/gateway";
import { ProductDetailActions } from "./ProductDetailActions";

type Product = { id: string; name: string; qty: number; price: number };

async function loadProduct(id: string): Promise<Product | null> {
  const base = serverGatewayBase();
  try {
    const res = await fetch(`${base}/products/${encodeURIComponent(id)}`, { cache: "no-store" });
    if (!res.ok) return null;
    return res.json();
  } catch {
    return null;
  }
}

export default async function ProductDetailPage({ params }: { params: { id: string } }) {
  const product = await loadProduct(params.id);
  if (!product) {
    return (
      <main className="page">
        <div className="empty-state">
          <p>Product not found.</p>
          <Link href="/products" className="btn btn--primary">
            Back to catalog
          </Link>
        </div>
      </main>
    );
  }
  return (
    <main className="page">
      <div className="page-header">
        <p className="muted small">
          <Link href="/products">← Catalog</Link>
        </p>
        <h1>{product.name}</h1>
        <p className="product-card__meta" style={{ marginTop: "0.5rem" }}>
          <span className="price">${product.price.toFixed(2)}</span>
          <span className="muted">{product.qty} in stock</span>
        </p>
      </div>
      <ProductDetailActions id={product.id} name={product.name} price={product.price} qty={product.qty} />
    </main>
  );
}

import Link from "next/link";
import { getServerSession } from "next-auth";
import { redirect } from "next/navigation";
import { authOptions } from "@/lib/auth";
import { gatewayFetch } from "@/lib/gateway";

type Order = {
  id: number;
  status: string;
  totalAmount: number;
  createdAt: string;
};

export default async function OrdersPage() {
  const session = await getServerSession(authOptions);
  if (!session?.user?.id) {
    redirect("/auth/signin?callbackUrl=/account/orders");
  }
  const res = await gatewayFetch("/orders", { method: "GET" });
  let orders: Order[] = [];
  if (res.ok) {
    orders = await res.json();
  }
  return (
    <main className="page">
      <div className="page-header">
        <h1>My orders</h1>
        <Link href="/" className="muted small">
          ← Home
        </Link>
      </div>
      {orders.length === 0 ? (
        <div className="empty-state">
          <p>No orders yet.</p>
        </div>
      ) : (
        <ul className="cart-list">
          {orders.map((o) => (
            <li key={o.id} className="cart-row" style={{ gridTemplateColumns: "1fr" }}>
              <div>
                <strong>Order #{o.id}</strong> — {o.status} — ${Number(o.totalAmount).toFixed(2)}
                <div className="muted small">{o.createdAt}</div>
              </div>
            </li>
          ))}
        </ul>
      )}
    </main>
  );
}

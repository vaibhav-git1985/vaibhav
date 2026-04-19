import Link from "next/link";

export default function CheckoutCancelPage() {
  return (
    <main>
      <h1>Checkout cancelled</h1>
      <p>No charge was completed.</p>
      <Link href="/checkout">Back to checkout</Link>
    </main>
  );
}

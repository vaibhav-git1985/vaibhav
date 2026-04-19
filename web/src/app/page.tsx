import Link from "next/link";
import { getServerSession } from "next-auth";
import { authOptions } from "@/lib/auth";

export default async function Home() {
  const session = await getServerSession(authOptions);
  return (
    <main className="page">
      <section className="hero">
        <h1>Demo microservices store</h1>
        <p>
          Browse the catalog, add items to your cart, sign in with Facebook or Okta, then check out with Stripe test
          mode. Delivery is mocked for learning.
        </p>
        <div className="hero__actions">
          <Link href="/products" className="btn btn--primary">
            Shop catalog
          </Link>
          <Link href="/cart" className="btn btn--on-hero">
            View cart
          </Link>
          {!session ? (
            <Link href="/auth/signin" className="btn btn--on-hero">
              Sign in
            </Link>
          ) : null}
        </div>
      </section>
      <p className="muted">
        Tip: configure <code>FACEBOOK_CLIENT_ID</code> / <code>FACEBOOK_CLIENT_SECRET</code> or Okta variables in{" "}
        <code>.env</code> for social login. Development sign-in stays available when <code>ALLOW_DEV_LOGIN=true</code>.
      </p>
    </main>
  );
}

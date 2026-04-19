import { getServerSession } from "next-auth";
import { authOptions } from "./auth";

/** Server-side (RSC, Route Handlers): prefer GATEWAY_URL (e.g. http://api-gateway:9292 in Docker). */
export function serverGatewayBase(): string {
  return (
    process.env.GATEWAY_URL ||
    process.env.NEXT_PUBLIC_GATEWAY_URL ||
    "http://localhost:9292"
  );
}

const base = () => serverGatewayBase();

export async function gatewayHeaders(): Promise<HeadersInit> {
  const session = await getServerSession(authOptions);
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
  };
  if (session?.accessToken) {
    headers.Authorization = `Bearer ${session.accessToken}`;
  }
  if (session?.user?.id) {
    headers["X-User-Sub"] = session.user.id;
  }
  return headers;
}

export async function gatewayFetch(path: string, init?: RequestInit) {
  const url = `${base()}${path.startsWith("/") ? path : `/${path}`}`;
  const headers = await gatewayHeaders();
  return fetch(url, {
    ...init,
    headers: { ...headers, ...(init?.headers as Record<string, string>) },
    cache: "no-store",
  });
}

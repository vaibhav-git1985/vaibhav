#!/usr/bin/env bash
# Seeds demo products via API Gateway (run from your machine while stack is up).
set -euo pipefail
GATEWAY="${GATEWAY_URL:-http://localhost:9292}"

post_product() {
  local id="$1" name="$2" qty="$3" price="$4"
  curl -sfS -X POST "${GATEWAY}/products" \
    -H "Content-Type: application/json" \
    -d "{\"id\":\"${id}\",\"name\":\"${name}\",\"qty\":${qty},\"price\":${price}}" \
    | head -c 200
  echo ""
}

echo "Seeding products to ${GATEWAY}/products ..."
post_product "demo-1" "Wireless earbuds" 40 49.99
post_product "demo-2" "USB-C hub (7-in-1)" 25 34.5
post_product "demo-3" "Mechanical keyboard" 15 129.0
post_product "demo-4" "Webcam 1080p" 30 79.99
post_product "demo-5" "Laptop stand (aluminum)" 50 45.0
echo "Done. Open http://localhost:3000/products (or GET ${GATEWAY}/products)"

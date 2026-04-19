import express from "express";
import { Queue, Worker } from "bullmq";
import IORedis from "ioredis";

const port = process.env.PORT || 3001;
const internalApiKey = process.env.INTERNAL_API_KEY || "change-me-internal";
const deliveryUrl = process.env.DELIVERY_SERVICE_URL || "http://localhost:8085";
const orderUrl = process.env.ORDER_SERVICE_URL || "http://localhost:8083";
const redisUrl = process.env.REDIS_URL || "redis://127.0.0.1:6379";

const connection = new IORedis(redisUrl, { maxRetriesPerRequest: null });

const deliverQueue = new Queue("deliver", { connection });

async function deliverOnce(orderId) {
	const shipRes = await fetch(`${deliveryUrl}/internal/shipments`, {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
			"X-Internal-Api-Key": internalApiKey,
		},
		body: JSON.stringify({ orderId }),
	});
	if (!shipRes.ok) {
		const t = await shipRes.text();
		throw new Error(`Delivery failed ${shipRes.status}: ${t}`);
	}
	const statusRes = await fetch(`${orderUrl}/internal/orders/${orderId}/status`, {
		method: "PUT",
		headers: {
			"Content-Type": "application/json",
			"X-Internal-Api-Key": internalApiKey,
		},
		body: JSON.stringify({ status: "SHIPPED" }),
	});
	if (!statusRes.ok) {
		const t = await statusRes.text();
		throw new Error(`Order status update failed ${statusRes.status}: ${t}`);
	}
}

new Worker(
	"deliver",
	async (job) => {
		const { orderId } = job.data;
		await deliverOnce(orderId);
	},
	{
		connection,
		concurrency: 5,
		defaultJobOptions: {
			attempts: 5,
			backoff: { type: "exponential", delay: 2000 },
			removeOnComplete: 1000,
		},
	},
);

const app = express();
app.use(express.json());

app.post("/internal/jobs/deliver", (req, res) => {
	const key = req.get("X-Internal-Api-Key");
	if (!key || key !== internalApiKey) {
		return res.status(403).json({ error: "forbidden" });
	}
	const orderId = req.body?.orderId;
	if (orderId == null || Number.isNaN(Number(orderId))) {
		return res.status(400).json({ error: "orderId required" });
	}
	const idempotencyKey = req.body?.idempotencyKey || `deliver-${orderId}`;
	const jobId = `deliver-${orderId}`;
	deliverQueue
		.add(
			"deliver",
			{ orderId: Number(orderId), idempotencyKey },
			{ jobId, removeOnComplete: true },
		)
		.then(() => res.status(202).json({ accepted: true, jobId }))
		.catch((err) => {
			const msg = String(err?.message || err || "");
			if (msg.includes("already exists") || msg.includes("Job") || msg.includes("duplicate")) {
				return res.status(202).json({ accepted: true, jobId, duplicate: true });
			}
			console.error(err);
			res.status(500).json({ error: "enqueue failed" });
		});
});

app.get("/health", (_req, res) => res.json({ ok: true }));

app.listen(port, () => console.log(`worker http listening on ${port}`));

import EventSource from "eventsource";

const BASE = "http://localhost:8080";
const USERNAME = "Test11";
const PASSWORD = "Test11";
const USER_ID = "19";

async function login() {
  const res = await fetch(`${BASE}/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username: USERNAME, password: PASSWORD }),
  });
  const json = await res.json();
  return json?.data?.token;
}

(async () => {
  const token = await login();
  if (!token) {
    console.error("Login failed: no token returned.");
    process.exit(1);
  }
  const headers = {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  };

  const start = Date.now();
  const es = new EventSource(`${BASE}/api/todo/remind/stream/${USER_ID}`, {
    headers: { Authorization: `Bearer ${token}` },
  });

  es.onerror = (err) => {
    console.error("SSE error:", err);
  };

  es.onmessage = (e) => {
    const now = Date.now();
    console.log("SSE event:", e.data);
    console.log("Latency(ms):", now - start);
    es.close();
    process.exit(0);
  };

  const timeoutMs = 3 * 60 * 1000;
  setTimeout(() => {
    console.error("Timeout waiting for reminder event.");
    es.close();
    process.exit(2);
  }, timeoutMs);

  const endTime = new Date(Date.now() + 2 * 60 * 1000).toISOString();
  const body = {
    userId: Number(USER_ID),
    title: "reminder test",
    content: "test",
    priority: 2,
    status: 0,
    date: new Date().toISOString().slice(0, 10),
    endTime,
    reminderEnabled: true,
    reminderOffset: 0,
  };

  await fetch(`${BASE}/api/todo/add`, {
    method: "POST",
    headers,
    body: JSON.stringify(body),
  });

  console.log("Todo created, waiting for reminder...");
})();
